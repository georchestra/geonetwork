/*
 * Copyright (C) 2009-2025 by the geOrchestra PSC
 *
 * This file is part of geOrchestra.
 *
 * geOrchestra is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * geOrchestra is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * geOrchestra.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.georchestra.geonetwork.security.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.fao.geonet.domain.User;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.georchestra.config.security.GeorchestraUserDetails;
import org.georchestra.geonetwork.security.AbstractGeorchestraIntegrationTest;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

public class GeorchestraPreAuthenticationFilterIT extends AbstractGeorchestraIntegrationTest {

    private @Autowired UsersApi consoleUsersApiClient;
    private @Autowired AccountsReconcilingService synchronizationService;

    private @Autowired GeorchestraPreAuthenticationFilter authFilter;
    private @Autowired ExternalizedSecurityProperties configProps;

    private MockHttpServletRequest request;
    private GeorchestraUser preAuthTestadmin;
    private String preAuthTestadminPayload;

    public @Before void before() {
        request = new MockHttpServletRequest();
        preAuthTestadmin = consoleUsersApiClient.findByUsername("testadmin").orElseThrow(NoSuchElementException::new);
        assertNotNull(preAuthTestadmin);
        preAuthTestadminPayload = support.jsonEncode(preAuthTestadmin);
    }

    public @Test void test_sec_proxy_request_header_is_mandatory() {

        // missing header sec-proxy: true
        request.addHeader("sec-user", this.preAuthTestadminPayload);
        User user = authFilter.getPreAuthenticatedPrincipal(request);
        assertNull(user);

        request.addHeader("sec-proxy", "true");
        user = authFilter.getPreAuthenticatedPrincipal(request);
        assertNotNull(user);
    }

    /**
     * Full pre-authorized payload request headers {@code sec-proxy=true} and
     * {@code sec-user=<json encoded GeorchestraUser>}
     */
    public @Test void test_full_canonical_user_payload_synchronizes_user_before_proceeding() {
        request.addHeader("sec-proxy", "true");
        request.addHeader("sec-user", preAuthTestadminPayload);

        User createdUponAuthentication = authFilter.getPreAuthenticatedPrincipal(request);

        assertNotNull(createdUponAuthentication);

        final GeorchestraUser consoleUser = preAuthTestadmin;
        assertEquals(consoleUser.getUsername(), createdUponAuthentication.getUsername());

        User found = synchronizationService.findUpToDateUserByUsername(consoleUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("user should have been synchronized"));

        assertNotNull(found);

        CanonicalUser canonical = mapper.toCanonical(consoleUser);
        support.assertUser(canonical, found);
    }

    /**
     * Legacy request headers {@code sec-proxy=true} and
     * {@code sec-username=<login name>}
     */
    public @Test void test_legacy_sec_username_header_synchronizes_user_before_proceeding() {
        final GeorchestraUser consoleUser = preAuthTestadmin;
        request.addHeader("sec-proxy", "true");
        request.addHeader("sec-username", consoleUser.getUsername());

        User createdUponAuthentication = authFilter.getPreAuthenticatedPrincipal(request);

        assertNotNull(createdUponAuthentication);

        User found = synchronizationService.findUpToDateUserByUsername(consoleUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("user should have been synchronized"));

        CanonicalUser canonical = mapper.toCanonical(consoleUser);
        support.assertUser(canonical, found);
    }

    public @Test void user_with_no_organization_is_allowed() {
        final GeorchestraUser testreviewer = consoleUsersApiClient.findByUsername("testreviewer")
                .orElseThrow(NoSuchElementException::new);
        assertNotNull(testreviewer);
        assertNull("test data error, testreviewer should have no org", testreviewer.getOrganization());
        String testreviewerPayload = support.jsonEncode(testreviewer);

        request.addHeader("sec-proxy", "true");
        request.addHeader("sec-user", testreviewerPayload);

        User gnUser = authFilter.getPreAuthenticatedPrincipal(request);
        assertNotNull(gnUser);
        assertNull(gnUser.getOrganisation());

        assertEquals(testreviewer.getUsername(), gnUser.getUsername());

        User found = synchronizationService.findUpToDateUserByUsername(testreviewer.getUsername())
                .orElseThrow(() -> new IllegalStateException("user should have been synchronized"));

        assertNotNull(found);

        CanonicalUser canonical = mapper.toCanonical(testreviewer);
        support.assertUser(canonical, found);
    }

    public @Test void user_with_roles_sync_should_sync_with_groups() {
        configProps.setSyncMode(GroupSyncMode.roles);
        configProps.setSyncRolesFilter(Pattern.compile("EL_(.*)"));
        synchronizationService.synchronize();
        final String testUserJwt = "{base64}eyJ1c2VybmFtZSI6InRlc3R1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIiwiUk9MRV9JTVBPUlQiLCJST0xFX0dOX0FETUlOIiwiUk9MRV9FTF9QU0MiLCJST0xFX0VMX0NPTVBBTlkiXSwib3JnYW5pemF0aW9uIjoiUFNDIiwiaWQiOiIwNDhiMmYzOC02ZWU3LTRlZWMtOWJlZC0zNDljYzZlYjEzYzMiLCJsYXN0VXBkYXRlZCI6ImY4ODJjNjJhZWY3M2VkZGNmMDgzYzUxNWYyNDlkMGZkYjMyM2U1NTA2Yzg4MTgwNzFiZDAyOWFjNGNiOWY4MTgiLCJmaXJzdE5hbWUiOiJUZXN0IiwibGFzdE5hbWUiOiJVU0VSIiwiZW1haWwiOiJwc2MrdGVzdHVzZXJAZ2VvcmNoZXN0cmEub3JnIiwibm90ZXMiOiJJbnRlcm5hbCBDUk0gbm90ZXMgb24gdGVzdHVzZXIiLCJsZGFwV2FybiI6ZmFsc2UsImlzRXh0ZXJuYWxBdXRoIjpmYWxzZX0=";
        request = new MockHttpServletRequest();
        request.addHeader("sec-proxy", "true");
        request.addHeader("sec-user", testUserJwt);
        request.addHeader("sec-orgname", "Project Steering Committee");
        request.addHeader("sec-external-authentication", "false");
        //Should not contains ROLE_ prefix
        assertThrows("Role EL_PSC not found in internal or external repository", IllegalArgumentException.class, () -> authFilter.getPreAuthenticatedPrincipal(request));
    }
}
