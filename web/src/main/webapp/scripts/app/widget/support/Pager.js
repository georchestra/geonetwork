Ext.ns("ExtGeoNet.widget.support");
/**
 * Plugin for PagingToolbar which replaces the textfield input with a slider 
 */
ExtGeoNet.widget.support.Pager = function(config) {
    if(config instanceof Function) {
        var changePage = config;
        config = {changePage: changePage};
    }
    ExtGeoNet.widget.support.Pager.superclass.constructor.call(this);
    Ext.apply(this, config);
};

Ext.extend(ExtGeoNet.widget.support.Pager, Object, {
    init : function(pbar){
        pbar.items.remove(pbar.refresh);
        var doAdd = this.doAdd;
        pbar.add = new Ext.Toolbar.Button({
            tooltip: this.addTip || this.addText || translate("add"),
            overflowText: this.addext || translate("add"),
            iconCls: this.addIconCls || 'md-add',
            handler: function() {doAdd(pbar);}, 
            scope: pbar
        });
        var doDuplicate = this.doDuplicate;
        pbar.dup = new Ext.Toolbar.Button({
            tooltip: this.dupTip || this.dupText || translate("duplicate"),
            overflowText: this.dupText || translate("duplicate"),
            iconCls: this.dupIconCls || 'md-dup',
            handler: function() {doDuplicate(pbar);}, 
            scope: pbar
        });
        var doDelete = this.doDelete;
        pbar.del = new Ext.Toolbar.Button({
            tooltip: this.deleteTip || this.deleteText || translate("delete"),
            overflowText: this.deleteText || translate("delete"),
            iconCls: this.deleteIconCls || 'md-delete',
            handler: function() {doDelete(pbar);}, 
            scope: pbar
        });
        pbar.insert(pbar.items.length-2,pbar.add);
        pbar.insert(pbar.items.length-2,pbar.dup);
        pbar.insert(pbar.items.length-2,pbar.del);
        var idx = pbar.items.indexOf(pbar.inputItem);
        
        Ext.each(pbar.items.getRange(idx - 2, idx + 2), function(c){
            c.hide();
        });
        var slider = new Ext.Slider({
            width: 114,
            minValue: 1,
            maxValue: 1,
            plugins: new Ext.slider.Tip({
                getText : function(thumb) {
                    return String.format('Page <b>{0}</b> of <b>{1}</b>', thumb.value, thumb.slider.maxValue);
                }
            }),
            listeners: {
                change: function(s, v) {
                    pbar.changePage(v);
                }
            }
        });
        pbar.insert(idx + 1, slider);
        
        pbar.on({
            change: function(pb, data){
                slider.setMaxValue(data.pages);
                slider.setValue(data.activePage);
            }
        });
    }
});