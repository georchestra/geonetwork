package org.fao.geonet.kernel.sharedobject;

import jeeves.interfaces.Service;
import jeeves.server.context.ServiceContext;
import org.jdom.Element;

import java.util.Collection;

/**
 * User: jeichar
 * Date: Jul 23, 2010
 * Time: 1:13:55 PM
 */
public class ListTypes extends AbstractSharedObjectService {
    public Element exec(Element params, ServiceContext context) throws Exception {
        Collection<String> types = dao(context).listTypes();

        Element results = new Element("types");
        for (String type : types) {
            results.addContent(new Element("type").addContent(new Element("name").setText(type)));
        }
        return results;
    }
}
