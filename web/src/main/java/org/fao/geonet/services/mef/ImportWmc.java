package org.fao.geonet.services.mef;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import jeeves.constants.Jeeves;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.IO;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.kernel.mef.MEFLib;
import org.fao.geonet.services.NotInReadOnlyModeService;
import org.jdom.Document;
import org.jdom.Element;

public class ImportWmc extends NotInReadOnlyModeService {

    private String styleSheetWmc;

    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {
        super.init(appPath, params);
        this.styleSheetWmc = appPath + Geonet.Path.IMPORT_STYLESHEETS + File.pathSeparator
                + "OGCWMC-to-ISO19139.xsl";
    }


    @Override
    public Element serviceSpecificExec(Element params, ServiceContext context)  throws Exception {
        String wmcString = Util.getParam(params, "wmc_string");
        String wmcUrl = Util.getParam(params, "wmc_url");
        String viewerUrl = Util.getParam(params, "viewer_url");

        // TODO: actually do something here

        // 1. JDOMize the string
        Element wmcDoc = Xml.loadString(wmcString, true);
        // 2. Apply XSL (styleSheetWmc)
        Element md = Xml.transform(wmcDoc, styleSheetWmc);

        System.out.println(Xml.getString(md));




        // 3. Change extra attributes (see parameters)
        // 4. Inserts metadata (owner is the one doing the action)



        Element result = new Element("id");
        result.setText("1");

        // --- return success with all metadata id
        return result;
    }
}
