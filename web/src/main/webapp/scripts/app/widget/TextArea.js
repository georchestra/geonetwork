Ext.ns('ExtGeoNet.widget');

ExtGeoNet.widget.TextArea = Ext.extend(Ext.form.TextArea, {
    /**
     * Initialize the component.
     * @private
     */
    initComponent : function() {
        ExtGeoNet.widget.support.Widget.config(this);
        ExtGeoNet.widget.TextArea.superclass.initComponent.call(this);
        this.updateFn(this,'change');
    }
    
});

Ext.reg('md-textarea', ExtGeoNet.widget.TextArea);
