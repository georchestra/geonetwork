package org.fao.geonet.proxy;

import javax.servlet.ServletException;

public class ConfigurableUriTemplateProxyServlet extends org.mitre.dsmiley.httpproxy.URITemplateProxyServlet {
    @Override
    protected void initTarget() throws ServletException {
        String propName = getConfigParam(P_TARGET_URI);
        targetUriTemplate = GeorchestraPropertyResolver.resolveProperty(propName);
    }
}
