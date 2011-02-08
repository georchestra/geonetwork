/**
 * 
 */
package org.fao.geonet.kernel.sharedobject;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import org.fao.geonet.kernel.sharedobject.support.Constants;
import org.jdom.Element;

/**
 * Lists all the objects of a particular shared object type
 *
 * @author jeichar
 */
public class Get extends AbstractSharedObjectService {

    public Element exec(Element params, ServiceContext context) throws Exception {

        Integer id = Integer.valueOf(Util.getParam(params, "id"));
        String type = Constants.TYPE_PREFIX+Util.getParam(params, "type");
        Element template = dao(context).get(type, id);

        return template;
    }
}