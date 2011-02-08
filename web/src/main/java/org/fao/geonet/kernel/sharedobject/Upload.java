/**
 * 
 */
package org.fao.geonet.kernel.sharedobject;

import jeeves.server.context.ServiceContext;

import jeeves.utils.Util;
import org.fao.geonet.csw.common.util.Xml;
import org.fao.geonet.kernel.sharedobject.support.Constants;
import org.jdom.Element;

/**
 * Uploads a shared object.
 * @author jeichar
 */
public class Upload extends AbstractSharedObjectService {

    public Element exec(Element params, ServiceContext context) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("recieved:\n"+Xml.getString(params));

        Integer id = Integer.valueOf(Util.getAttrib(params,"id"));
        String typename = Constants.TYPE_PREFIX + Util.getAttrib(params,"type");
        Element value = (Element) params.getChildren().iterator().next();

        dao(context).update(typename,id,value);
        return params;
    }

}
