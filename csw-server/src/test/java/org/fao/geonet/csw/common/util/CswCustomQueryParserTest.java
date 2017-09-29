package org.fao.geonet.csw.common.util;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CswCustomQueryParserTest {

    @Test
    public void testCswCustomQueryParserNegative() throws QueryNodeException {
        CswCustomQueryParser ccqp = new CswCustomQueryParser();
        assertTrue(ccqp.parse("-_groupPublished: csw-filtered", "title").toString()
                .equals("-_groupPublished:csw-filtered"));
    }

    @Test
    public void testCswCustomQueryParserPositive() throws QueryNodeException {
        CswCustomQueryParser ccqp = new CswCustomQueryParser();
        assertTrue(ccqp.parse("_groupPublished: csw-filtered", "title").toString()
                .equals("_groupPublished:csw-filtered"));
    }
    
    @Test
    public void testCswCustomQueryParserMoreThoroughTest() throws QueryNodeException {
        CswCustomQueryParser ccqp = new CswCustomQueryParser();
        assertTrue(ccqp.parse("_groupPublished: csw-filtered "
                + "-_anotherFiedNegated: bbb _anotherAnotherField: blah", "title").toString()
                .equals("_groupPublished:csw-filtered -_anotherFiedNegated:bbb _anotherAnotherField:blah"));
    }
    
}
