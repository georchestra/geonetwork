package org.fao.geonet.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class allows to resolve variables from the georchestra.datadir/geonetwork/geonetwork.properties
 * file at runtime.
 *
 * Using servlets from dsmiley.httproxy group does not allow to easily configure them outside of
 * the web.xml. This class provides a static method to do so, which is called from the 2 other
 * classes from the package which extend the ones from dsmiley's package.
 *
 * @see {org.fao.geonet.ConfigurableProxyServlet} and {org.fao.geonet.ConfigurableUriTemplateProxyServlet}.
 */
public class GeorchestraPropertyResolver {

    public static String resolveProperty(String name) {
        File props = Paths.get(System.getProperty("georchestra.datadir"),
            "geonetwork", "geonetwork.properties").toFile();
        if (! props.exists() || ! props.canRead()) {
            throw new RuntimeException("unable to load geonetwork.properties from the geOrchestra datadir");
        }
        Properties gnProps = new Properties();

        try(InputStream fis = new FileInputStream(props)) {
            gnProps.load(fis);
        } catch (Exception ex) {
            throw new RuntimeException("unable to read geonetwork.properties from the geOrchestra datadir");
        }
        return gnProps.getProperty(name);
    }
}
