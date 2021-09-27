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

import javax.servlet.http.HttpServletRequest;

import org.fao.geonet.domain.User;
import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.model.CanonicalUser;
import org.georchestra.commons.security.SecurityHeaders;
import org.georchestra.config.security.GeorchestraSecurityProxyAuthenticationFilter;
import org.georchestra.config.security.GeorchestraUserDetails;
import org.georchestra.geonetwork.security.integration.CanonicalModelMapper;
import org.georchestra.security.model.GeorchestraUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.google.common.annotations.VisibleForTesting;

/**
 * Pre-auth filter that gets the credentials as a {@link GeorchestraUser} from a
 * {@link GeorchestraSecurityProxyAuthenticationFilter} using composition, and
 * makes sure GeoNetwork user matches the georchestra user info.
 * 
 * @see AccountsReconcilingService
 */
public class GeorchestraPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    static final Logger log = LoggerFactory.getLogger(GeorchestraPreAuthenticationFilter.class.getPackage().getName());
    /**
     * Delegate to get the {@link GeorchestraUserDetails} and return the matching
     * GeoNetwork {@link User} from {@link #userLinkService}
     */
    private GeorchestraSecurityProxyAuthenticationFilter delegate = new GeorchestraSecurityProxyAuthenticationFilter();

    /**
     * Manages 1:1 relationship between {@link GeorchestraUserDetails} and
     * {@link User}
     */
    private @Autowired AccountsReconcilingService userLinkService;

    private @Autowired CanonicalModelMapper modelMapper;

    @Override
    public @VisibleForTesting User getPreAuthenticatedPrincipal(HttpServletRequest request) {
        final GeorchestraUserDetails auth = delegate.getPreAuthenticatedPrincipal(request);
        User user = null;
        if (auth == null) {
            log.debug("geOrchestrea pre-auth not provided. URI: %s", request.getRequestURI());
        } else if (auth.isAnonymous()) {
            log.debug("geOrchestrea pre-auth is anonymous. URI: %s", request.getRequestURI());
        } else {
            final GeorchestraUser authenticatedUser = checkMandatoryProperties(auth.getUser());
            final CanonicalUser canonicalizedUser = modelMapper.toCanonical(authenticatedUser);

            user = userLinkService//
                    .findUpToDateUser(canonicalizedUser)//
                    .orElseGet(() -> userLinkService.forceMatchingGeonetworkUser(canonicalizedUser));
        }
        return user;
    }

    private GeorchestraUser checkMandatoryProperties(GeorchestraUser user) {
        requireNonNull(user.getId(), "GeorchestraUserDetails.userId is mandatory");
        requireNonNull(user.getOrganization(), "GeorchestraUserDetails.organization");
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("GeorchestraUserDetails.roles is mandatory");
        }
        requireNonNull(user.getLastUpdated(), "GeorchestraUserDetails.lastUpdated is mandatory");
        return user;
    }

    /**
     * @return {@code true} if the request comes from georchestra's security proxy
     */
    @Override
    protected Boolean getPreAuthenticatedCredentials(HttpServletRequest request) {
        return Boolean.parseBoolean(SecurityHeaders.decode(request.getHeader(SEC_PROXY)));
    }
}
