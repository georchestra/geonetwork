/**
 * 
 */
package org.fao.geonet.kernel.sharedobject;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import jeeves.utils.Util;
import jeeves.utils.Xml;
import org.fao.geonet.kernel.sharedobject.support.Constants;
import org.geotools.data.FeatureStore;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;

/**
 * Lists all the objects of a particular shared object type
 * 
 * @author jeichar
 */
public class List extends AbstractSharedObjectService {

    private static String stripNsStyleSheet;

    @Override
    public void init(String appPath, ServiceConfig config) throws Exception {
        super.init(appPath, config);
        stripNsStyleSheet = appPath+"/xsl/sharedobject/strip-ns.xsl";
    }

    public Element exec(Element params, ServiceContext context) throws Exception {

        String type = Constants.TYPE_PREFIX+ Util.getParam(params, "type");
        String search = Util.getParam(params, "search", null);
        Results resultType = Results.lookup(Util.getParam(params, "results", ""));

        java.util.List<Element> templates = dao(context).list(type, search, resultType);

        Element count = new Element("count").setText(""+templates.size());
        Element results = new Element("sharedobjects").addContent(count).addContent(templates);

        return results;
    }
    public enum Results {
        IDS {
            private String[] ID = new String[]{Constants.ID_ATTRIBUTE_NAME};
            public String[] attributes(FeatureStore<SimpleFeatureType, SimpleFeature> store) {
               return ID;
            }
            @Override
            public Element toElem(SimpleFeature f) throws JDOMException, IOException {
                final String id = f.getAttribute(Constants.ID_ATTRIBUTE_NAME).toString();
                return new Element("id").setText(id);
            }
        }, NO_NS{
            public String[] attributes(FeatureStore<SimpleFeatureType, SimpleFeature> store) {
               return FULL.attributes(store);
            }
            @Override
            public Element toElem(SimpleFeature feature) throws Exception{
                return Xml.transform(FULL.toElem(feature), stripNsStyleSheet);
            }
        },
        FULL {
            public String[] attributes(FeatureStore<SimpleFeatureType, SimpleFeature> store) {
               return null;
            }
            @Override
            public Element toElem(SimpleFeature feature) throws JDOMException, IOException {
                final String data = (String) feature.getAttribute(Constants.DATA_ATTRIBUTE_NAME);
                Element dataXml = Xml.loadString(data, false);
                dataXml.removeAttribute("id");
                final String id = feature.getAttribute(Constants.ID_ATTRIBUTE_NAME).toString();
                return new Element("object")
                            .addContent(new Element("id").setText(id))
                            .addContent(new Element("data").addContent(dataXml));

            }};

        private static Results lookup(String name) {
            for (Results r : values()) {
                if(r.name().equalsIgnoreCase(name)) {
                    return r;
                }
            }
            return FULL;
        }

        public abstract String[] attributes(FeatureStore<SimpleFeatureType, SimpleFeature> store);

        public abstract Element toElem(SimpleFeature features) throws Exception, IOException;
    }
}
