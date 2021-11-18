/*
 * Copyright (C) 2021 by the geOrchestra PSC
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.NoSuchElementException;

import org.fao.geonet.domain.User;
import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.model.CanonicalUser;
import org.georchestra.geonetwork.security.AbstractGeorchestraIntegrationTest;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

public class GeorchestraPreAuthenticationFilterIT extends AbstractGeorchestraIntegrationTest {

    private @Autowired UsersApi consoleUsersApiClient;
    private @Autowired AccountsReconcilingService synchronizationService;

    private @Autowired GeorchestraPreAuthenticationFilter authFilter;

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
}
