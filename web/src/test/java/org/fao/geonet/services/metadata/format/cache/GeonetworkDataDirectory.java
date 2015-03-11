package org.fao.geonet.services.metadata.format.cache;

import java.io.File;


/**
 * Just a stub class waiting for migration to GN 3.
 * @author pmauduit
 *
 */
public class GeonetworkDataDirectory {

    public File getHtmlCacheDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
    
}
