package org.fao.geonet.services.mef;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerFactory;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.services.NotInReadOnlyModeService;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.XSLTransformer;

public class ImportWmc extends NotInReadOnlyModeService {

    private String styleSheetWmc;

    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {
        super.init(appPath, params);
        this.styleSheetWmc = appPath + Geonet.Path.IMPORT_STYLESHEETS + File.separator
                + "OGCWMC-to-ISO19139.xsl";
    }


    @Override
    public Element serviceSpecificExec(Element params, ServiceContext context)  throws Exception {
        String wmcString = Util.getParam(params, "wmc_string");
        String wmcUrl = Util.getParam(params, "wmc_url");
        String viewerUrl = Util.getParam(params, "viewer_url");

        // TODO: actually do something here

        Map<String,String> xslParams = new HashMap<String,String>();
        xslParams.put("viewer_url", viewerUrl);
        xslParams.put("wmc_url", wmcUrl);

        // 1. JDOMize the string
        Element wmcDoc = Xml.loadString(wmcString, false);
        // 2. Apply XSL (styleSheetWmc)
        Element transformedMd = Xml.transform(wmcDoc, styleSheetWmc, xslParams);

        // 3. Change extra attributes (see parameters)
        // TODO: might be done before (parametrized)



        // 4. Inserts metadata

        Element result = new Element("id");
        result.setText("1");

        // --- return success with all metadata id
        return result;
    }
}
