package org.georchestra.geonetwork.security.authentication;

import static org.georchestra.commons.security.SecurityHeaders.SEC_PROXY;

import javax.servlet.http.HttpServletRequest;

import org.fao.geonet.domain.User;
import org.georchestra.commons.security.SecurityHeaders;
import org.georchestra.config.security.GeorchestraSecurityProxyAuthenticationFilter;
import org.georchestra.config.security.GeorchestraUserDetails;
import org.georchestra.geonetwork.security.integration.GeorchestraToGeonetworkUserReconcilingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "org.georchestra.geonetwork.security.authentication")
public class GeorchestraAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private GeorchestraSecurityProxyAuthenticationFilter delegate = new GeorchestraSecurityProxyAuthenticationFilter();

    private @Autowired GeorchestraToGeonetworkUserReconcilingService userLinkService;

    @Override
    protected User getPreAuthenticatedPrincipal(HttpServletRequest request) {
        final GeorchestraUserDetails georchestraUserDetails = delegate.getPreAuthenticatedPrincipal(request);
        if (georchestraUserDetails == null || georchestraUserDetails.isAnonymous()) {
            return null;
        }

        User gnUser = userLinkService.getMatchingGeonetworkUser(georchestraUserDetails);
        return gnUser;
    }

    /**
     * @return {@code true} if the request comes from georchestra's security proxy
     */
    @Override
    protected Boolean getPreAuthenticatedCredentials(HttpServletRequest request) {
        return Boolean.parseBoolean(SecurityHeaders.decode(request.getHeader(SEC_PROXY)));
    }
}
