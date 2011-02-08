package org.fao.geonet.services.resources;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.services.publisher.GeoFile;
import org.geotools.referencing.CRS;
import org.jdom.Element;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;

/**
 * Set the CRS of a data file.  Only works for data with a prj file
 */
public class SetCRS implements Service {
    public void init(String s, ServiceConfig serviceConfig) throws Exception {

    }

    public Element exec(Element params, ServiceContext context) throws Exception {
        String file = Util.getParam(params, "zip");
        String code = Util.getParam(params, "epsgCode");
        String access = Util.getParam(params, "access");
        String metadataId = Util.getParam(params, "metadataId");

        CoordinateReferenceSystem crs = CRS.decode(code);
        File dir = new File(Lib.resource
                .getDir(context, access, metadataId));
        File f = new File(dir, file);

        GeoFile geoFile = new GeoFile(f);
        geoFile.setCRS(f, crs);
        return null;
    }
}
