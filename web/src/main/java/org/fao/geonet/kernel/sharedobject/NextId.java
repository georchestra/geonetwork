package org.fao.geonet.kernel.sharedobject;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import org.fao.geonet.kernel.sharedobject.support.Constants;
import org.jdom.Element;

/**
 * User: jeichar
 * Date: Jul 26, 2010
 * Time: 4:14:31 PM
 */
public class NextId extends AbstractSharedObjectService {

    public Element exec(Element params, ServiceContext context) throws Exception {
        String type = Constants.TYPE_PREFIX+Util.getParam(params, "type");
        int id = dao(context).nextId(type);
        Element newObj = new Element("new").setContent(new Element("id").setText(""+id));
        return newObj;
    }
}