Ext.ns("sharedobject.ObjectGrid");

sharedobject.ObjectGrid.createStore = function(type, config) {
    Ext.applyIf(config,{
        autoLoad: true,
        autoDestroy: true,
        remoteSort: false,
        storeId: type+'-store',
        url: Env.locService+'/xml.sharedobject.list?results=no_ns&type='+type,
        record: "object",
        totalRecords: "count",
        idPath: 'id'
    });
    var objDef = Ext.data.Record.create(config.fields);
    var myReader = new Ext.data.XmlReader(config, objDef);
    
    config.reader = myReader;
    return new Ext.data.Store(config);
};
sharedobject.ObjectGrid.createGrid = function(type, store, config, gridListeners) {
    Ext.applyIf(config,{
        id: 'grid-'+type,
        store: store,
        mode: 'remote',
        selModel: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        listeners: gridListeners
    });
    return new Ext.grid.GridPanel(config);
};

/*
 * example options:
 * {
        type:type, (required)
        maskElemId:'grid', (options)
        gridConfig: object, (optional)
        success: function(grid) {...} (required) 
        failure: function(response) {...} (options)
        gridListeners: {
            dblclick: function() {...}
        }
    } 
 */
sharedobject.ObjectGrid.create = function(options) {
    var maskElemId = options.maskElemId || Ext.getBody(); 
    var myMask = new Ext.LoadMask(Ext.get(maskElemId), {msg:"Please wait..."});
    myMask.show();
    
    Ext.Ajax.request({
        url: Env.locService+'/xml.sharedobject.jsgridspec',
        method: 'GET',
        success: function(response) {
            myMask.hide();
            var json = Ext.util.JSON.decode(response.responseText);
            var store = sharedobject.ObjectGrid.createStore(options.type, json.store);
            var gridConf = Ext.apply(json.grid, options.gridConfig);
            Ext.each(json.colModel, function(r) {
                r.header = translate(r.header);
            });
            gridConf.colModel = new Ext.grid.ColumnModel(json.colModel);
            var grid = sharedobject.ObjectGrid.createGrid(options.type, store, gridConf, options.gridListeners);
            
            options.success(response,grid);
        },
        failure: function(response) {
            myMask.hide();
            if(options.failure !== undefined) {
                options.failure (response);
            } else {
                alert("unexpected failure listing "+options.type);
            }
        },
        params: {type: options.type}
    });
};