Ext.ns("ExtGeoNet.widget.support");
/**
 * Basic Widget for handling multiple nodes.  Subclasses or configuration must define the page function.
 * page will return the text component to display as a page.  Page will take a XML node to display within that page.
 */
ExtGeoNet.widget.support.PagingWidget = Ext.extend(Ext.form.FieldSet, {
    /**
    * Initialize the component.
    * @private
    */
    initComponent : function() {
        var widget = this;


        this.title = this.fieldLabel;
        this.fieldLabel = undefined;
        this.autoHeight = true;
        this.layout = 'card';
        this.activeItem = 0;
        this.deferredRender = true;
        this.items = Functional.map(this.page, this.nodes);

        if(this.nodes instanceof Array) {
            var store = new Ext.data.Store({
                proxy: new Ext.ux.data.PagingMemoryProxy(this.nodes),
                reader: new Ext.data.ArrayReader({fields:[]})
            });
            this.bbar = new Ext.PagingToolbar({
                store : store,
                displayInfo: true,
                pageSize: 1,
                beforePageText: translate('Contact'),
                plugins: new ExtGeoNet.widget.support.Pager({
                    doAdd: function(pbar) {
                        // TODO non-prototype implementation
                        var newNode = widget.nodes[0].cloneNode(true);
                        ExtGeoNet.Xml.removeText(newNode);
                        widget.nodes.push(newNode);
                        store.reload();
                        widget.add(widget.page(newNode));
                        pbar.changePage(widget.items.length);
                    },    
                    doDuplicate: function(pbar) {
                        // TODO non-prototype implementation
                        var newNode = widget.nodes[widget.activeItem || 0].cloneNode(true);
                        widget.nodes.push(newNode);
                        store.reload();
                        widget.add(widget.page(newNode));
                        pbar.changePage(widget.items.length);
                    },
                    doDelete: function(pbar) {
                        // TODO non-prototype implementation
                        alert("Need to implement delete");
                    },
                    addTip: widget.pagerConfig.addTip,
                    addText: widget.pagerConfig.addText,
                    addIconCls: widget.pagerConfig.addIconCls,

                    deleteTip: widget.pagerConfig.deleteTip,
                    deleteText: widget.pagerConfig.deleteText,
                    deleteIconCls: widget.pagerConfig.deleteIconCls,

                    dupTip: widget.pagerConfig.dupTip,
                    dupText: widget.pagerConfig.dupText,
                    dupIconCls: widget.pagerConfig.dupIconCls

                }),
                listeners: {
                    change: function(pbar, selection){
                        if((selection.activePage - 1) != widget.activeItem) {
                            widget.activeItem = selection.activePage - 1;
                            widget.layout.setActiveItem(widget.activeItem);
                        }
                    }
                }
            });
        }

        ExtGeoNet.widget.support.PagingWidget.superclass.initComponent.call(this);
    }
});
