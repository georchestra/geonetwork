Ext.namespace('GeoNetwork.admin');

GeoNetwork.admin.PrivilegesPanel = Ext.extend(Ext.grid.GridPanel, {
    
    defaultConfig: {
        autoScroll: true,
        cls: 'privileges-panel'
    },
    
    /** admin service url
     * ex : http://localhost:8080/geonetwork/srv/fre/xml.metadata.admin.form?id=543 **/
    url : undefined,

    /**
     * id of the metadata for which privileges are loaded
     */
    id : undefined,
    
    /**
     * Return true is the <on/> element exists in the n element where <id> 
     * match with the field name.
     * this is a reference to the Ext.data.Field.
     * 
     * Field with name 'oper0' will retrieve this element
     * 
     * <oper>
     *   <id>0</id>
     *   <on/>
     * </oper>
     * 
     * and return true
     */
    convertOper: function(v,n) {
        var id = this.name.charAt(this.name.length-1 );
        var idElt = Ext.DomQuery.selectNode('oper/id:nodeValue('+id+')',n);
        return idElt && idElt.nextElementSibling ? true : false ;
    },
    
    /**
     * Return the basic Ext.data.Field config for <oper> elements
     */
    getOperField : function(id) {
        return { 
            name: 'oper' + id,
            type: 'bool',
            convert: this.convertOper
        }
    },
    
    initComponent : function() {
        
        Ext.applyIf(this, this.defaultConfig);
        
        // Read <groups> elements
        var groupReader = new Ext.data.XmlReader({
            record: 'group',
            idPath: 'id',
            totalRecords: '@TotalResults',
            fields: ['name','description',
                { 
                    name: 'oper', 
                    convert: this.convertOper
                 }
            ]
        });
        
        // Read <operations> element to get columns definition
        var operationsStore = new Ext.data.XmlStore({
            record: 'record',
            idPath: 'id',
            totalRecords: '@TotalResults',
            fields: ['id','name', 'reserved', 
                 {
                    name: 'label',
                    mapping : 'label > ' + app.getCatalogue().lang
                 }
            ]
        });
        
        // top bar with text field filter
        this.tbar = this.tbar || [{
            xtype: 'box',
            autoEl: {
                tag: 'img',
                cls: 'filter-text-icon'
            }
        },{
            xtype: 'textfield',
            width: 150,
            enableKeyEvents: true,
            listeners: {
                'keyup' : {
                    // Filter the grid store
                    fn: function(txtF, event) {
                        store.filter('name', txtF.getValue());
                    }
                }
            }
        }];
        
        // bottom bar with submit button
        this.bbar = this.bbar || [{
            text: OpenLayers.i18n('save'),
            handler: function() {
                var args={};
                var submitFn = function(group) {
                    
                    
                    for (var i=0;i<this.colModel.config.length-1;i++) {
                        var di = this.colModel.config[i].dataIndex;
                        if(di.indexOf('oper') == 0 && group.get(di)) {
                            args['_' + group.id + '_' + di.charAt(di.length-1 )] ='on';
                        }
                    }
                };
                
                // if the store is filtered, use the snapshot collection
                if(this.store.snapshot) {
                    this.store.snapshot.each(submitFn,this);
                }
                else {
                    this.store.each(submitFn,this);
                }
                
                // update privileges
                args.id = this.id;
                args.timeType = 'on';
                
                Ext.Ajax.request({
                    url : app.getCatalogue().services.mdAdminSave,
                    disableCaching: false,
                    params: args
                });
                
                if(this.ownerCt.getXType() == 'window') {
                    this.ownerCt.close();
                }
            },
            scope: this
        }];
        
        // empty config (store + cm) to initiate the first time the gridPanel
        // then the real grid panel is loaded on store.load callback with reconfigure()
        this.store= new Ext.data.Store();
        this.cm = new Ext.grid.ColumnModel({
            columns:[]
        });
        
        // Store the grid is based on : contains all groups informations
        var store = new Ext.data.XmlStore({
            autoDestroy: true,
            storeId: 'privilegesStore',
            url: this.url, 
            record: 'group',
            idPath: 'id',
            totalRecords: '@TotalResults',
            fields: [
                 'name',
                 'description', {
                     name: 'label',
                     convert: function(v,n) {
                         var label = Ext.DomQuery.selectNode('label/'+app.getCatalogue().lang,n);
                         return label ? label.textContent : n.getElementsByTagName('name')[0].textContent ;
                     }
                 },
                 this.getOperField(0),
                 this.getOperField(1),
                 this.getOperField(2),
                 this.getOperField(3),
                 this.getOperField(4),
                 this.getOperField(5),
                 this.getOperField(6), {
                     name: 'all',
                     type: 'bool',
                     defaultValue: false
                 }
                 
             ]
        });

        // Load the store to get the Ext.grid.ColumnModel, the view and the datas
        store.load({
            callback: function(recs,opt,suc) {
                operationsStore.loadData(Ext.DomQuery.selectNode('operations',store.reader.xmlData));
                
                // Disable groups you d'ont have rights on
                var groupOwner = store.reader.xmlData.getElementsByTagName('groupOwner')[0].textContent;
                var isOwner = store.reader.xmlData.getElementsByTagName('owner')[0].textContent;
                
                var columns = [{
                    id: 'group',
                    header: 'Group',
                    dataIndex: 'label',
                    width: 220
                }];
                
                // build column depending on operations Store
                operationsStore.each(function(rec) {
                    columns.push({
                        xtype: 'checkcolumn',
                        header: rec.get('label'),
                        dataIndex: 'oper'+rec.get('id'),
                        width: 80,
                        align: 'center'
                    });
                });
                
                columns.push({
                    xtype: 'checkcolumn',
                    header: OpenLayers.i18n('all'),
                    dataIndex: 'all',
                    width: 80,
                    align: 'center'
                });
                
                var cm = new Ext.grid.ColumnModel({
                    defaults: {
                        sortable: false,
                        hideable: false,
                        menuDisabled: true
                    },
                    columns: columns
                });
                
                // grid view to disable rows depending on rights
                this.getView().getRowClass = function(record, index) {
                    if(record.id == 0 || record.id == -1 || record.id == 1) {
                        if(app.getCatalogue().isAdmin()) {
                            return '';
                        }
                        else {
                            return 'privileges-grid-disable';
                        }
                        
                    }
                    else if(isOwner == 'false') {
                        return 'privileges-grid-disable';
                    }
                    else {
                        return '';
                    }
                };
                
                this.reconfigure(store,cm);
            },
            scope: this
        });
        GeoNetwork.admin.PrivilegesPanel.superclass.initComponent.call(this);
    }
});
