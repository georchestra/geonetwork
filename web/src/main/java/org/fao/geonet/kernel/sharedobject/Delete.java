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
public class Delete extends AbstractSharedObjectService {

    public Element exec(Element params, ServiceContext context) throws Exception {
        String[] ids = Util.getParam(params, "ids").split(",");
        String type = Constants.TYPE_PREFIX+Util.getParam(params, "type");
        dao(context).delete(type, ids);
        return null;
    }
}
