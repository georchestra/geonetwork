package org.fao.geonet.kernel.sharedobject.support;

import org.jdom.Element;

/**
 * Takes the data from a sharedobject and normalizes the pertinent data for doing
 * comparisons, searches, etc... on the object.
 * 
 * @author jeichar
 */
public interface Normalizer {
    void init(String appPath);
    String normalize(Element data) throws Exception;
    String normalize(String query);
}
