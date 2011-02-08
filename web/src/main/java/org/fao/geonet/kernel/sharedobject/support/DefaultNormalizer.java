package org.fao.geonet.kernel.sharedobject.support;

import jeeves.utils.Xml;
import org.jdom.Element;

/**
 * Normalizer that removes accents and capitalization
 *  
 * @author jeichar
 */
public class DefaultNormalizer implements Normalizer {
    private String styleSheetPath;


    public void init(String appPath){
        this.styleSheetPath = appPath+"xsl/sharedobject/normalize.xsl";
    }

    public String normalize(Element data) throws Exception {
        Element normalized = Xml.transform(data, styleSheetPath);
        return normalize(normalized.getTextNormalize());
    }

    public String normalize(String query) {
        String lowercase = query.toLowerCase();
        
        return java.text.Normalizer.normalize(lowercase, java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
