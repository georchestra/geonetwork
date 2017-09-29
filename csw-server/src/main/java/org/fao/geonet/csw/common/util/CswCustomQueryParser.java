package org.fao.geonet.csw.common.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;

public class CswCustomQueryParser extends StandardQueryParser {

    public CswCustomQueryParser() {
        super();
        setSyntaxParser(new CswCustomSyntaxParser());
        setEnablePositionIncrements(true);
    }

    public CswCustomQueryParser(Analyzer analyzer) {
        this();
        this.setAnalyzer(analyzer);
    }
    
}
