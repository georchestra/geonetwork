Ext.ns('ExtGeoNet.widget.iso19139');

ExtGeoNet.widget.iso19139.MultiContact = Ext.extend(ExtGeoNet.widget.support.PagingWidget, {
    initComponent: function() {
        ExtGeoNet.widget.iso19139.MultiContact.superclass.initComponent.call(this);
    },
    pagerConfig: {
        addTip: translate("addContact"),
        addText: translate("addContact"),
        addIconCls: 'md-add-contact',
        
        deleteTip: translate("deleteContact"),
        deleteText: translate("deleteContact"),
        deleteIconCls: 'md-delete-contact',

        dupTip: translate("dupContact"),
        dupText: translate("dupContact"),
        dupIconCls: 'md-dup-contact'
    },
    page: function(node) {
        return {
            nodes: node,
            xtype: 'md-iso19139-contact'
        };
    }
});

Ext.reg('md-iso19139-multi-contact', ExtGeoNet.widget.iso19139.MultiContact);
