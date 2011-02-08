package org.fao.geonet.kernel.sharedobject;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import jeeves.utils.Xml;
import org.fao.geonet.kernel.sharedobject.support.Constants;
import org.jdom.Element;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;

/**
 * User: jeichar
 * Date: Jul 23, 2010
 * Time: 1:13:55 PM
 */
public class JsGridSpec extends AbstractSharedObjectService {
    public static final String GRID_SPEC_DIR = "/xml/sharedobject/js-grid-spec/";

    public Element exec(Element params, ServiceContext context) throws Exception {

        String type = Util.getParam(params, "type");
        BufferedReader jsonFile = new BufferedReader(new FileReader(appPath + GRID_SPEC_DIR + type + ".json"));
        StringBuilder json = new StringBuilder();
        String line = jsonFile.readLine();
        while(line != null) {
            json.append(line);
            line = jsonFile.readLine();
        }
        return new Element("json").setText(json.toString());
    }
}