package org.fao.geonet.services.index;

import java.util.Vector;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

/**
 * A service for accessing the lists of terms of a particular lucene field.
 * 
 * Currently not accessible by clients.
 * 
 * @author jeichar
 */
public class ListTerms implements Service {

    private String _fld;

    @SuppressWarnings("unchecked") 
    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        Element terms = new Element("terms");
        
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        for(String term : (Vector<String>) gc.getSearchmanager().getTerms(_fld)){
            Element tElem = new Element("term");
            tElem.setText(term);
            terms.addContent(tElem);
        }
        
        return terms;
    }

    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {
        _fld = params.getValue("fld");
        
    }

}
