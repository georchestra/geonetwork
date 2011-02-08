Ext.ns('ExtGeoNet.widget');
ExtGeoNet.widget.Support = {
    create: function(xtype,nodes,config) {
        var required = {
              nodes: nodes,
              xtype: xtype
          };
        return Ext.apply(config, required);
    }
};
ExtGeoNet.widget.DefaultMapping = {
    ABSTRACT : function(node, config) { 
        config.updateFn = ExtGeoNet.widget.support.Widget.multilingualUpdate;
        return ExtGeoNet.widget.Support.create('md-textarea', node, config);
    },
    KEYWORDS : function(node, config) { 
        config.updateFn = ExtGeoNet.widget.support.Widget.multilingualUpdate;
        return ExtGeoNet.widget.Support.create('md-keywords', node, config);
    },
    XML : function(node, config) {
        return ExtGeoNet.widget.Support.create('md-xmlarea', node, config);
    },
    URL : function(node, config) {
        config.updateFn = ExtGeoNet.widget.support.Widget.simpleUpdate;
        config.vtype = 'url';
        return ExtGeoNet.widget.Support.create('md-textfield', node, config);
    },
    def : function(node, config) {
        config.updateFn = ExtGeoNet.widget.support.Widget.multilingualUpdate;
        if (node instanceof Array) { 
          return ExtGeoNet.widget.Support.create('md-multitextfield', node, config);
        } else {
          return ExtGeoNet.widget.Support.create('md-textfield', node, config);
        }
    }
};