Ext.ns('ExtGeoNet.widget');

ExtGeoNet.widget.TextArea = Ext.extend(Ext.form.TextArea, {
    /**
     * Initialize the component.
     * @private
     */
    initComponent : function() {
        var  nodes = this.nodes;
        this.value = ExtGeoNet.Xml.parser.write(nodes);
        ExtGeoNet.widget.TextArea.superclass.initComponent.call(this);
        this.on('change', function(text, newValue, oldValue){
            try {
                ExtGeoNet.appendChildren(nodes, newValue);
            } catch(e) {
               setValue(ExtGeoNet.Xml.parser.write(nodes));
               alert(e);
            }
        });
    }
    
});

Ext.reg('md-xmlarea', ExtGeoNet.widget.TextArea);
