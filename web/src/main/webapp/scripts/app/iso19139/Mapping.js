Ext.ns("ExtGeoNet.config");

ExtGeoNet.config.Mapping = {
    CONTACT : function(nodes, config) {
        if(nodes instanceof Array) {
            return ExtGeoNet.widget.Support.create('md-iso19139-multi-contact', nodes, config); 
        } else {
            config.labelInBorder = true;
            return ExtGeoNet.widget.Support.create('md-iso19139-contact', nodes, config); 
        }
    }
};