package org.fao.geonet.kernel.sharedobject.support;

import java.io.IOException;

import org.geotools.data.FeatureStore;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * A mapping to and from the xml data of a shared object and 
 * the Feature representation of that object 
 * 
 * @author jeichar
 */
public interface Mapper {
    SimpleFeature setData(int id, Element data, String searchData, SimpleFeature feature);
    Element toData(SimpleFeature feature) throws IOException, JDOMException;
    Normalizer getNormalizer();
    Filter lookupFilter(int id);
    Filter searchFilter(String search);
    int nextId(FeatureStore<SimpleFeatureType, SimpleFeature> store) throws IOException;
}
