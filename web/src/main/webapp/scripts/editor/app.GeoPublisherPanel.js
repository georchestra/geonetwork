Ext.namespace("app");

/**
 * Class: GeoPublisherPanel
 */
app.geoservers = {};

var Geoserver = Ext.data.Record.create([{
    name: 'id'
}, {
    name: 'name'
}, {
    name: 'adminurl'
}, {
    name: 'wmsurl'
}, {
    name: 'wfsurl'
}, {
    name: 'namespacePrefix'
}]);

app.geoservers.geoserverStore = new Ext.data.Store({
    proxy: new Ext.data.HttpProxy({
        url: 'geoserver.publisher?action=LIST',
        method: 'GET'
    }),
    baseParams: {
        action: 'LIST'
    },
    reader: new Ext.data.XmlReader({
        record: 'node',
        id: 'id'
    }, Geoserver),
    fields: ['id', 'name', 'adminurl', 'wmsurl', 'wfsurl', 'namespacePrefix'],
    sortInfo: {
        field: 'name'
    }
});

/**
 * Panel for datasets publication in Geoserver nodes.
 * Datasets could be a zip file (for ESRI Shapefiles)
 * or GeoTiff.
 */
/*
 * TODO : handle multiple file in zip
 * 
 * When uploading a zip file, GeoServer create a datastore
 * based on zip filename and a featureType based on dataset filename.
 * 
 * Only featureType could be added to metadata record (in online source information).
 * 
 * Current Java service create temporary files with unique name, so we lost
 * dataset names (which is probably not fine for end users).
 */ 
app.GeoPublisherPanel = Ext.extend(Ext.FormPanel, {
    border: false,
    
    /**
     * Property: itemSelector
     */
    itemSelector: null,
    
    /**
     * Property: loadingMask
     */
    loadingMask: null,
    
    /**
     * Property: metadataId
     */
    metadataId: null,
    
    /**
     * Property: fileName    Name of the file to publish
     * Could not contains "." in it as it will fail to be displayed. FIXME
     */
    fileName: null,
    
    /**
     * Property: accessStatus     private/publid
     */
    accessStatus: null,
    
    /**
     * Property: nodeId        Id of geoserver node.
     */
    nodeId: null,
    
    /**
     * Property: geoPublicationMap
     */
    geoPublicationMap: null,
    
    /**
     * Property: geoPublicationMapPanel
     */
    geoPublicationMapPanel: null,
    
    /**
     * Property: geoPublicationTb Toolbar for geo publication.
     */
    geoPublicationTb: null,
    
    statusBar: null,
    
     /**
     * Property: layerPreviewName     Name of the layer for dataset preview
     */
    layerPreviewName: 'DatasetPreview',

    updateEPSGCode: function(epsgCode){
        Ext.Ajax.request({
            url: 'resources.setcrs',
            timeout: 60000,
            params: {
                epsgCode: epsgCode,
                metadataId: this.metadataId,
                zip: this.fileName,
                access: this.accessStatus
            },
            method: 'GET',
            success: function(result, request){
                this.loadingMask.hide();
                this.publishData();
            },
            failure: function(result, request){
                this.statusBar.setText(translate('epsgCodeLookupFailure'));
                this.loadingMask.hide();
            },
            scope: this
        });
    },
    publishData: function(){
        this.statusBar.setText('');
        this.loadingMask.show();

        Ext.Ajax.request({
            url: 'geoserver.publisher',
            timeout: 60000,
            params: {
                metadataId: this.metadataId,
                nodeId: this.nodeId,
                zip: this.fileName,
                access: this.accessStatus,
                action: 'CREATE'
            },
            method: 'GET',
            success: function(result, request){
                // Check exceptions
                //  * In case of GeoNetwork OutOfMemoryError (could happen on big file during copy)
                if (result.responseText.indexOf('OutOfMemoryError') != -1) {
                    this.statusBar.setText(translate('publishError') +
                        translate('publishErrorCode') +
                        ' OutOfMemoryError.');
                    this.loadingMask.hide();
                    return;
                }

                //    * In case of GeoServer REST error message
                var root = result.responseXML.getElementsByTagName('Exception').item(0);
                if (root !== null) {
                    if(root.getAttribute('status') === 'epsgCodeLookupFailure') {
                    this.loadingMask.hide();
                        Ext.MessageBox.prompt(
                            'EPSG Code',
                            'The EPSG Code for the data was not found.  Please enter the EPSG code:',
                            function(button,code) {
                                if("ok" === button) {this.updateEPSGCode(code);}
                            },
                            this,
                            false,
                            "EPSG:2154");
                    } else {
                        this.statusBar.setText(translate('publishError') + ": " +
                            translate(root.getAttribute('status')));
                        this.loadingMask.hide();
                    }
                    return;
                }


                this.statusBar.setText(translate('publishSuccess'));

                // Try to display layer on map preview
                var map = Ext.getCmp('mapPanel').map;
                this.cleanLayerPreview();
                var layer = new OpenLayers.Layer.WMS(this.layerPreviewName, app.geoservers.geoserverStore.getById(this.nodeId).get('wmsurl'), {
                    transparent: 'true',
                    layers: this.fileName.substr(0, this.fileName.indexOf('.')) // Use newly published dataset
                });

                map.addLayer(layer);
                this.statusBar.setText(this.statusBar.text + translate('publishLayerAdded'));
                this.loadingMask.hide();
            },
            failure: function(result, request){
                this.statusBar.setText(translate('publicationFailed'));
                this.loadingMask.hide();
            },
            scope: this
        });
    },

    initComponent: function(){
        /**
         * Publish current file in remote node.
         */
        var publishAction = new Ext.Action({
            text: translate('publish'),
            tooltip: translate('publishTooltip'),
            iconCls: 'addVector',
            handler: this.publishData,
            scope: this
        });
        
        /**
         * Unpublish current file.
         */
        var unpublishAction = new Ext.Action({
            text: translate('unpublish'),
            tooltip: translate('unpublishTooltip'),
            iconCls: 'delVector',
            handler: function(){
                this.statusBar.setText('');
                this.cleanLayerPreview();
                
                Ext.Ajax.request({
                    url: 'geoserver.publisher',
                    params: {
                        metadataId: this.metadataId,
                        nodeId: this.nodeId,
                        zip: this.fileName,
                        access: this.accessStatus,
                        action: 'DELETE'
                    },
                    method: 'GET',
                    success: function(result, request){
                        /*
                         * FIXME : unpublication return <Exception status="got status code 404" />
                         * but dataset is unpublished.
                         *  
                         var root = result.responseXML.getElementsByTagName('Exception').item(0);
                        if (root != null) {
                            this.statusBar.setText(translate('unpublishError') +
                            translate('publishErrorCode') +
                            root.getAttribute('status'));
                            return;
                        }*/
                        this.statusBar.setText(translate('unpublishSuccess'));
                    },
                    failure: function(result, request){
                        this.statusBar.setText(translate('unpublishError'));
                    },
                    scope: this
                });
            },
            scope: this
        });
        
        
        /**
         * Check current file is already published in remote node.
         */
        var getAction = new Ext.Action({
            text: translate('check'),
            iconCls: 'connect',
            handler: function(){
                this.statusBar.setText('');
                
                Ext.Ajax.request({
                    url: 'geoserver.publisher',
                    params: {
                        metadataId: this.metadataId,
                        nodeId: this.nodeId,
                        zip: this.fileName,
                        access: this.accessStatus,
                        action: 'GET'
                    },
                    method: 'GET',
                    success: function(result, request){
                        // Return error message according to exception
                        var report = result.responseText;
                        if (report.indexOf('Connection refused') != -1) {
                            this.statusBar.setText(translate('errorConnectionRefused'));
                            return;
                        }
                        else {
                            if (report.indexOf('404') != -1) {
                                this.statusBar.setText(translate('errorDatasetNotFound'));
                                return;
                            }
                        }
                        this.statusBar.setText(translate('datasetFound'));
                    },
                    failure: function(result, request){
                        this.statusBar.setText(translate('checkFailure'));
                    },
                    scope: this
                });
            },
            scope: this
        });
        
        
        /**
         * Add online source information to current metadata record.
         */
        var addOnLineSourceAction = new Ext.Action({
            text: translate('addOnlineSource'),
            iconCls: 'processMetadata',
            tooltip: '',
            handler: function(){
                var node = app.geoservers.geoserverStore.getById(this.nodeId);
                this.fireEvent('addOnLineSource', this, node.get('wmsurl'), node.get('namespacePrefix') + ":" + this.fileName.substr(0, this.fileName.indexOf('.')), '');
            },
            scope: this
        });
        
        
        
        this.geoPublicationTb = new Ext.Toolbar({
            disabled: true, // Disabled by default when no node selected
            items: [new Ext.Button(getAction), new Ext.Button(publishAction), new Ext.Button(unpublishAction), new Ext.Button(addOnLineSourceAction)]
        });
        
        this.statusBar = new Ext.form.Label({
            id: 'statusBar',
            html: translate('statusInformation')
        });
        
        var tb = new Ext.Toolbar({
            items: [this.statusBar]
        });
        
        
        app.geoservers.geoserverStore.load({
            add: false
        });
        
        
        
        this.items = [this.geoPublicationTb, {
            xtype: 'panel',
            layout: 'fit',
            bodyStyle: 'padding: 5px;',
            border: false,
            items: [this.getGeoserverCombo()]
        }, this.getGeoPublicationMapPanel(), tb];
        
        
        /**
         * triggered when the user has published a dataset
         */
        this.addEvents('addOnLineSource');
        
        
        app.GeoPublisherPanel.superclass.initComponent.call(this);
        
        
        // add mask when action publish which could take some time
        // according to zip file size.
        if (!this.loadingMask) {
            this.loadingMask = new Ext.LoadMask(Ext.getBody(),    // TODO : maybe restrict loading mask to panel ? 
                    {msg: translate('publishing')});
        }
    },
    
    /**
     * Create a map panel to have a quick preview of the published datasets
     */
    getGeoPublicationMapPanel: function(){

        OpenLayers.ImgPath = "../../scripts/openlayers/img/";
        var geoPubConfig = Geonetwork.CONFIG.GeoPublisher;
		var options = geoPubConfig.mapOptions;
        var map = extentMap.map = new OpenLayers.Map(options);

        this.geoPublicationMapPanel = {
                xtype: 'gx_mappanel',
                id: 'mapPanel',
                title: translate('mapPreview'),
                map: map,
                layers: geoPubConfig.layerFactory(),
                autoWidth: true,
                height: 350
            };
        return this.geoPublicationMapPanel;
    },
    
    /**
     * Remove layer preview
     */
    cleanLayerPreview: function() {
        var map = Ext.getCmp('mapPanel').map;
        var currentLayers = map.getLayersByName(this.layerPreviewName);
        if (currentLayers.length > 0) {
            currentLayers[0].destroy();
        }
    },
    
    /**
     * Create a combo with the list of geoserver node registerd.
     * When selecting a node:
     *  - check current server status (TODO).
     *  - check current dataset is already published, assume
     *  featuretype name = dataset name (which is the case when
     *  dataset published using this interface).
     */
    getGeoserverCombo: function(){
        return {
            xtype: 'combo',
            tpl: '<tpl for="."><div ext:qtip="{name}. {adminurl}" class="x-combo-list-item">{name}</div></tpl>',
            store: app.geoservers.geoserverStore,
            displayField: 'name',
            typeAhead: true,
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all',
            emptyText: translate('selectANode'),
            selectOnFocus: true,
            id: 'geoserverNode',
            listeners: {
                select: function(combo, record, index){
                    this.nodeId = record.get('id');
                    this.geoPublicationTb.enable();
                    // TODO : onchange check datastore already exist or not disabled button accordingly ?                
                    // TODO : check nodeId is up
                },
                clear: function(combo){
                    //
                },
                scope: this
            }
        };
    },
    
    /**
     * APIMethod: setRef Set the element reference : metadata
     * identifier and file name
     */
    setRef: function(id, fileName, accessStatus){
        this.metadataId = id;
        this.fileName = fileName;
        this.accessStatus = accessStatus;
    }
    
});
