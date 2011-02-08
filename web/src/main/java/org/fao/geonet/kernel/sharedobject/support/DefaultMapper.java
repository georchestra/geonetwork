package org.fao.geonet.kernel.sharedobject.support;

import java.io.IOException;

import org.fao.geonet.csw.common.util.Xml;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

import static org.fao.geonet.kernel.sharedobject.support.Constants.DATA_ATTRIBUTE_NAME;
import static org.fao.geonet.kernel.sharedobject.support.Constants.ID_ATTRIBUTE_NAME;
import static org.fao.geonet.kernel.sharedobject.support.Constants.SEARCH_ATTRIBUTE_NAME;

public class DefaultMapper implements Mapper {
    private FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());

    private Normalizer normalizer;

    // protected by a lock
    private int nextId;

    public DefaultMapper(Normalizer normalizer) {
        this.normalizer = normalizer;
    }

    public SimpleFeature setData(int id, Element data, String searchData, SimpleFeature feature) {
        feature.setAttribute(DATA_ATTRIBUTE_NAME, Xml.getString(data));
        feature.setAttribute(SEARCH_ATTRIBUTE_NAME, searchData);
        feature.setAttribute(ID_ATTRIBUTE_NAME, id);
        return feature;
    }

    public Element toData(SimpleFeature feature) throws IOException, JDOMException {
        return Xml.loadString((String) feature.getAttribute(DATA_ATTRIBUTE_NAME), false);
    }

    public Normalizer getNormalizer() {
        return normalizer;
    }

    public Filter lookupFilter(int id) {
        return filterFactory.equals(filterFactory.property(ID_ATTRIBUTE_NAME), filterFactory.literal(id));

    }

    public Filter searchFilter(String search) {
        Expression searchProp = filterFactory.property(SEARCH_ATTRIBUTE_NAME);
        return filterFactory.like(searchProp, search);
    }

    public synchronized int nextId(FeatureStore<SimpleFeatureType, SimpleFeature> store) throws IOException {
        if (nextId == -1) {
            nextId = 1;
            Query query = new DefaultQuery(store.getSchema().getTypeName(), Filter.INCLUDE, new String[]{ID_ATTRIBUTE_NAME});
            FeatureIterator<SimpleFeature> features = store.getFeatures(query).features();
            int max = 0;
            while(features.hasNext()) {
                SimpleFeature next = features.next();
                int id = (Integer)next.getAttribute(Constants.ID_ATTRIBUTE_NAME);
                if(id > max) {
                    max = id;
                }
            }
        }
        nextId += 1;
        return nextId - 1;
    }
}
