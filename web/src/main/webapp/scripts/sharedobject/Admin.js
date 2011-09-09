Ext.ns("sharedobject.Admin");


sharedobject.Admin.typeStore = new Ext.data.Store({
    autoLoad: true,
    autoDestroy: true,
    remoteSort: false,
    storeId: 'typestore',
    reader: new Ext.data.XmlReader({
	fields: [{name:'type', mapping:'name'}],
	record: 'type'
    }),
    proxy: new Ext.data.HttpProxy({
        url: Env.locService+'/xml.sharedobject.types.list',
	method: 'GET'
     }),
     idPath: 'type'
});
sharedobject.Admin.typeCombo = new Ext.form.ComboBox({
    id: 'type-combo',
    store: sharedobject.Admin.typeStore,
    displayField: 'type',
    typeAhead: true,
    mode: 'remote',
    forceSelection: true,
    triggerAction: 'all',
    emptyText: translate('type.select'),
    selectOnFocus: true,
    width: 135,
    listeners: {
        select: function(combo, record) {
             sharedobject.Admin.changeType(record.get('type'));
        }
    }
});
sharedobject.Admin.currentGrid = function() {
    var items=sharedobject.Admin.gridContainer.items;
    var activeItem = sharedobject.Admin.gridContainer.layout.activeItem.id;
    var grid = items.find(function(i){
        return activeItem === i.id;
    });
    grid.type = grid.id.substring(5);
    return grid;

};
sharedobject.Admin.deleteHandler = function() {
    var grid = sharedobject.Admin.currentGrid();
    var selections = grid.getSelectionModel().getSelections();
    var ids = "";
    Ext.each(selections, function(i){
        if(ids.length > 0) {
            ids += ",";
        }
        ids += i.id;
    });

    if(ids.length > 0) {
        Ext.Ajax.request({
            url: Env.locService+'/xml.sharedobject.delete',
            success: function(response) {
                grid.getStore().reload();
                alert("deleted row "+ids[0]+" from "+grid.getStore().storeId);
            },
            failure: function() {
                alert("An unexpected error occured during deletion.");
            },
            params: {type: grid.type, ids:ids}
        });
    }
};
sharedobject.Admin.createHandler = function() {
    var grid = sharedobject.Admin.currentGrid();
    Ext.Ajax.request({
        url: Env.locService+'/xml.sharedobject.nextid',
        success: function(response) {
            var id = Ext.DomQuery.selectValue("id",response.responseXML);
            if(id !== undefined && id !== null) {
                window.open(Env.locService+"/sharedobject.edit?id="+id+"&type="+grid.type, "_newtab");
            }
        },
        failure: function() {
            alert("An unexpected error occured during creation of a new object.");
        },
        params: {type: grid.type}
    });
};
sharedobject.Admin.toolbar = new Ext.Toolbar({
    items: [
        {
            xtype: 'button',
            text: translate('refresh'),
            cls: 'md-btn',
            disabled: true,
            handler: function() { sharedobject.Admin.currentGrid().getStore().reload(); }
        },{
            xtype: 'button',
            text: translate('create'),
            cls: 'md-btn',
            disabled: true,
            handler: sharedobject.Admin.createHandler
        },
        {
            xtype: 'button',
            text: translate('edit'),
            cls: 'md-btn',
            disabled: true,
            handler: function() {
                var grid = sharedobject.Admin.currentGrid();
                if(grid !== undefined && grid.getSelectionModel().getSelected() !== undefined ) {
                    var selected = grid.getSelectionModel().getSelected().get('id');
                    window.open(Env.locService+"/sharedobject.edit?id="+selected+"&type="+grid.type, "_newtab");
                }
            }
        },
        {
            xtype: 'button',
            text: translate('delete'),
            cls: 'md-btn',
            disabled: true,
            handler: sharedobject.Admin.deleteHandler
        },
        '->',
        sharedobject.Admin.typeCombo
    ]
});

sharedobject.Admin.changeType = function(type) {
    var id = 'grid-'+type;
    var grid = sharedobject.Admin.gridContainer.findById(id);

    if (grid === null) {
        sharedobject.ObjectGrid.create({
            id:id,
            type:type,
            maskElemId:'grid',
            success: function(response,grid) {
                sharedobject.Admin.toolbar.items.each (function(i) {i.setDisabled(false);});
                sharedobject.Admin.gridContainer.add(grid);
                sharedobject.Admin.gridContainer.layout.setActiveItem(grid.id);
            },
            failure: function(response) {
                sharedobject.Admin.toolbar.items.each (function(i) {
                    if(i.id != 'type-combo') {i.setDisabled(true);}
                });
                myMask.hide();
                sharedobject.Admin.gridContainer.layout.setActiveItem(sharedobject.Admin.typeGrid.id);
            },
            gridListeners: {
                dblclick: function() {
                    var selected = this.getSelectionModel().getSelected().get('id');
                    window.open(Env.locService+"/sharedobject.edit?id="+selected+"&type="+type, "_newtab");
                }
            }
        });
    } else {
        sharedobject.Admin.gridContainer.layout.setActiveItem(id);
    }

};
sharedobject.Admin.typeGrid = {
    xtype: 'grid',
    id: 'grid-type',
    store: sharedobject.Admin.typeStore,
    mode: 'remote',
    autoExpandColumn: 'type',
    selModel: new Ext.grid.RowSelectionModel({
        singleSelect: true
    }),
    colModel: new Ext.grid.ColumnModel( [
        {header: translate("type"), id: 'type', sortable: true}
    ]),
    listeners: {
        dblclick: function() {
            var selected = this.getSelectionModel().getSelected().get('type');
            sharedobject.Admin.typeCombo.setValue(selected);
            sharedobject.Admin.changeType(selected);
        }
    }
};
sharedobject.Admin.gridContainer = new Ext.Panel({
            region: 'center',
            id: 'grid',
            border: false,
            layout: 'card',
            activeItem: 0, 
            tbar: sharedobject.Admin.toolbar,
            items: sharedobject.Admin.typeGrid
        });
sharedobject.Admin.init = function() {
    if (!Ext.getDom('north')) {
	return;
    }
    new Ext.Viewport({
        layout: 'border',
        items: [
            {
                region: 'north',
                contentEl: 'north',
                autoHeight: true,
                border: false
            }, sharedobject.Admin.gridContainer 
        ]
    });
};

Ext.onReady(sharedobject.Admin.init);

