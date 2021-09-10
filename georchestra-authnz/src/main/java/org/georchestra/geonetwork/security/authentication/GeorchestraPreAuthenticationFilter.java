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

import static java.util.Objects.requireNonNull;
import static org.georchestra.commons.security.SecurityHeaders.SEC_PROXY;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.fao.geonet.domain.User;
import org.georchestra.commons.security.SecurityHeaders;
import org.georchestra.config.security.GeorchestraSecurityProxyAuthenticationFilter;
import org.georchestra.config.security.GeorchestraUserDetails;
import org.georchestra.geonetwork.logging.Logging;
import org.georchestra.geonetwork.security.integration.GeorchestraToGeonetworkUserReconcilingService;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.GeorchestraUserHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class GeorchestraPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private static final Logging log = Logging.getLogger("org.georchestra.geonetwork.security.authentication");

    /**
     * Delegate to get the {@link GeorchestraUserDetails} and return its matching
     * {@link User}
     */
    private GeorchestraSecurityProxyAuthenticationFilter delegate = new GeorchestraSecurityProxyAuthenticationFilter();

    /**
     * Manages 1:1 relationship between {@link GeorchestraUserDetails} and
     * {@link User}
     */
    private @Autowired GeorchestraToGeonetworkUserReconcilingService userLinkService;

    @Override
    protected User getPreAuthenticatedPrincipal(HttpServletRequest request) {
        final GeorchestraUserDetails auth = delegate.getPreAuthenticatedPrincipal(request);
        if (auth == null) {
            log.debug("geOrchestrea pre-auth not provided. URI: ", request.getRequestURI());
            return null;
        }
        if (auth.isAnonymous()) {
            log.debug("geOrchestrea pre-auth is anonymous. URI: ", request.getRequestURI());
            return null;
        }
        final GeorchestraUser user = auth.getUser();
        ensureLastUpdatedPropertyIsProvidedOrCreateIt(user);
        log.info("pre-auth: " + auth);
        checkMandatoryFields(user);

        Optional<User> uptodateUser = userLinkService.findUpToDateUser(user);
        return uptodateUser.orElseGet(() -> userLinkService.forceMatchingGeonetworkUser(user));
    }

    private void checkMandatoryFields(GeorchestraUser user) {
        requireNonNull(user.getId(), "GeorchestraUserDetails.userId is mandatory");
        requireNonNull(user.getOrganization().getId(), "GeorchestraUserDetails.organization.id is mandatory");
        requireNonNull(user.getOrganization().getName(), "GeorchestraUserDetails.organization.name is mandatory");
        if (user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("GeorchestraUserDetails.roles is mandatory");
        }
        requireNonNull(user.getLastUpdated(), "GeorchestraUserDetails.lastUpdated is mandatory");
    }

    private void ensureLastUpdatedPropertyIsProvidedOrCreateIt(GeorchestraUser georchestraUser) {
        if (null == georchestraUser.getLastUpdated()) {
            String hash = GeorchestraUserHasher.createLastUpdatedUserHash(georchestraUser);
            log.info("lastUpdated not provided for user %s(%s), using a hash based on relevant fields: %s",
                    georchestraUser.getId(), georchestraUser.getUsername(), hash);
            georchestraUser.setLastUpdated(hash);
        }
    }

    /**
     * @return {@code true} if the request comes from georchestra's security proxy
     */
    @Override
    protected Boolean getPreAuthenticatedCredentials(HttpServletRequest request) {
        return Boolean.parseBoolean(SecurityHeaders.decode(request.getHeader(SEC_PROXY)));
    }
}
