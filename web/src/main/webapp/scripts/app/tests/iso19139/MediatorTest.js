Ext.ns("ExtGeoNet.test.iso19139");

ExtGeoNet.test.iso19139.Mediator = {
    test : function (noui) {
        if(ExtGeoNet.config.schema != "iso19139") {return;}
        
        var dom = ExtGeoNet.test.SampleData.dom();
        var assert = ExtGeoNet.test.Assert.assert;
        var assertObjEquals = ExtGeoNet.test.Assert.assertObjEquals;
        
        assert(['deu','eng','fra'], ExtGeoNet.config.Mediator.language.list(dom));
        assert('deu', ExtGeoNet.config.Mediator.language.main(dom));

        var titleNode = ExtGeoNet.Xml.find(dom, "gmd:title")[0];
        var expectedTranslations = {
            'DE' : 'JE_EI DEU',
            'EN' : 'JE_EI ENG',
            'FR' : 'JE_EI FRA'
        };
        var titleTrans = ExtGeoNet.config.Mediator.CharString.read(titleNode);
        assertObjEquals(expectedTranslations, titleTrans);

        var abstractNode = ExtGeoNet.Xml.find(dom, "gmd:abstract")[0];
        expectedTranslations = {
            'DE' : 'JE_EI abstract'
        };
        var abstractTrans = ExtGeoNet.config.Mediator.CharString.read(abstractNode);
        ExtGeoNet.test.Assert.intercept("assertObjEquals(expectedTranslations, abstractTrans)", "(abstr) read CharString without trim");
        
        assertObjEquals(expectedTranslations, ExtGeoNet.config.Mediator.CharString.read(abstractNode,true), "(abst) read CharString trim");
    }
};