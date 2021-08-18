package org.fao.geonet.proxy;

import org.apache.http.client.utils.URIUtils;

import javax.servlet.ServletException;
import java.net.URI;

public class ConfigurableProxyServlet extends org.mitre.dsmiley.httpproxy.ProxyServlet {
    @Override
    protected void initTarget() throws ServletException {
        String propName = getConfigParam(P_TARGET_URI);
        targetUri = GeorchestraPropertyResolver.resolveProperty(propName);
        // checks this is a valid uri
        try {
            targetUriObj = new URI(targetUri);
        } catch (Exception e) {
            throw new ServletException("Trying to process targetUri init parameter: "+e,e);
        }
        targetHost = URIUtils.extractHost(targetUriObj);
    }
}
