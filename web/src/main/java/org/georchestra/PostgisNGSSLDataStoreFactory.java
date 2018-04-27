package org.georchestra;

import java.io.IOException;
import java.util.Map;

import org.geotools.data.postgis.PostgisNGDataStoreFactory;


public class PostgisNGSSLDataStoreFactory extends PostgisNGDataStoreFactory {

    private final static String SSL = "ssl";

    private final static String SSL_FACTORY = "sslfactory";

    @Override
    protected String getJDBCUrl(Map params) throws IOException {
        String jdbcUrl = super.getJDBCUrl(params);
        if (params.containsKey(SSL)) {
            jdbcUrl += "?ssl=" + params.get(SSL);
            if (params.containsKey(SSL_FACTORY)) {
                jdbcUrl += "&sslfactory=" + params.get(SSL_FACTORY);
            }
        }
        return jdbcUrl;
    }
}
