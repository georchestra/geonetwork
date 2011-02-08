Ext.ns('ExtGeoNet');
Ext.ns('ExtGeoNet.config');


// default config for all editors
ExtGeoNet.config.hideBanner = true;
ExtGeoNet.config.getMetadataURL = function(mdId){
    return Env.locService+'/metadata.ext.edit.data?id='+mdId;
};
ExtGeoNet.config.saveMetadataURL = function(){
    return Env.locService+'/metadata.ext.save';
};
ExtGeoNet.config.saveParams = function(mdId){
    return 'id="'+mdId+'"';
};


ExtGeoNet.Main = {
  // metadata being edited
    metadata: null,
    
    // combo for selecting the language being edited
    languageCombo:null,
    error: function (msg,title) {
        title = title === undefined ? translate("Error") : title;
        Ext.Msg.show({
               title: title,
               msg: msg,
               icon: Ext.MessageBox.ERROR
        });
    },
    onlineInit : function(mdId) {
        ExtGeoNet.Main.mdId = mdId;
        ExtGeoNet.Main.init(function (success) {
            Ext.Ajax.request({
                url: ExtGeoNet.config.getMetadataURL(mdId),
                
                success: function(response) {
                    if(!response.responseXML) {
                        ExtGeoNet.Main.error("Expected xml response but was: "+response.responseText);
                    } else {
                      success(response.responseXML);
                    }
                },
                failure: function(error) {
                    ExtGeoNet.Main.error("Failure to load metadata for "+mdId+".<br/>msg: "+error.statusText+"<br/>code: "+error.status);
                }
            });
        });
    },
    init : function (loadMetadata) {
        if (ExtGeoNet.config === undefined || ExtGeoNet.config.Layouts === undefined ||
                ExtGeoNet.config.Layouts.items === undefined) {
            ExtGeoNet.Main.error("ExtGeoNet.config.Layouts.items is not defined.");
            return;
        }

        Ext.QuickTips.init();
        
        loadMetadata(function (md) {
          ExtGeoNet.Main.metadata = md;

          ExtGeoNet.Main.createLangCombo();
          ExtGeoNet.Main.createViewport();
        });
    },
    
    createLangCombo: function() {
          var langArray = function(l) {
                var e = Ext.util.Format.trim(l);
                return [ExtGeoNet.loc.Languages.convA3ToA2(e), e, translate(e)];
            };
            var languages = Functional.map(langArray, ExtGeoNet.config.Mediator.language.list(ExtGeoNet.Main.metadata));

            var languageStore = new Ext.data.ArrayStore({
            fields: ['abbr2', 'abbr3','name'],
            data : languages
            });
        ExtGeoNet.Main.languageCombo = new Ext.form.ComboBox({
                store: languageStore,
                displayField: 'name',
                mode: 'local',
                triggerAction: 'all',
                autoSelect: true,
                editable: false,
                selectOnFocus: true,
                forceSelection: true,
                typeAhead: true,
                width: 135,
                listClass: 'lang-combo-list',
                iconCls: 'no-icon',
                hidden: !ExtGeoNet.config.isMultilingual,
                value: languageStore.getAt(0).get('name') // TODO this is dependent on the Metadata languages
            });
        ExtGeoNet.Main.languageCombo.getLanguage = function() {
          var data = languageStore.findExact('name', ExtGeoNet.Main.languageCombo.value);
          return languageStore.getAt(data).data.abbr2;
        };
    },
    saveButton : function() {
        var saveCls = 'md-save';
        var busyCls = 'md-small-spinner';
        return {
            xtype: 'button',
            text: translate('save'),
            iconCls: saveCls,
            cls: 'md-btn',
            handler: function(button, evt) {
                var conn = new Ext.data.Connection({
                    method: 'POST',
                    listeners: {
                        beforerequest: function() {
                            this.setIconClass(busyCls);
                            this.setDisabled(true);
                        },
                        requestcomplete: function() {
                            this.setIconClass(saveCls);
                            this.setDisabled(false);
                        },
                        requestexception: function(e) {
                            this.setDisabled(false);
                            this.setIconClass(saveCls);
                        },
                        scope: button
                    }
                });
              var p = ExtGeoNet.Xml.parser;
              var params = ExtGeoNet.config.saveParams(ExtGeoNet.Main.mdId);
              var updateDoc = p.read('<update '+params+'></update>');
              var Xml = ExtGeoNet.Xml;
              var importedNode = Xml.importNode(updateDoc, ExtGeoNet.Main.metadata, true);
              p.getChildEl(updateDoc).appendChild(importedNode);

              conn.request({
                  url: ExtGeoNet.config.saveMetadataURL(),
                  xmlData: updateDoc
              });
            }
        };
    },
    createToolbar: function(){
    
        var tb = new Ext.Toolbar({
            items: [ExtGeoNet.Main.saveButton(), {xtype: 'tbfill'},  ExtGeoNet.Main.languageCombo]
        });
        return tb;
    },

    createViewport: function() 
    {
        var tabGroups = Functional.map(ExtGeoNet.Main.createTabGroup, ExtGeoNet.config.Layouts.items);
        var mainPanel;
        if(tabGroups.length > 1 || tabGroups[0].items.length > 1) {
            mainPanel = {
              xtype: 'grouptabpanel',
              tabWidth: 130,
              activeGroup: 0,
              items: tabGroups
            };
        } else {
              mainPanel = tabGroups[0].items[0];
              mainPanel.header = false;
              mainPanel.border = false;
        }
        new Ext.Viewport({
        layout: 'border',
        items: [{
            region: 'north',
            contentEl: 'north',
            autoHeight: true,
            border: false,
            hidden: ExtGeoNet.config.hideBanner
          },{
            region: 'center',
            border: false,
            layout: 'fit',
            tbar: ExtGeoNet.Main.createToolbar(),
            items : [mainPanel]
          }]
    });
    },
    
    
    createTabGroup : function(dfn) {
      var tabs = [ExtGeoNet.Main.createTab(dfn)];
      if (dfn.children !== undefined) {
        tabs = tabs.concat(Functional.map(ExtGeoNet.Main.createTab, dfn.children));
      }
      return {
          expanded: true,
          items: tabs
        };
    },
    
    createTab : function (dfn) {
      return {
        title: dfn.title,
        iconCls: dfn.iconCls,
        autoScroll: true,
        tabTip: dfn.tabTip,
        style: 'padding: 10px;',
        layout: 'fit',
            items: [
              ExtGeoNet.Main.createPanel(dfn)
            ]
        };
    },
    createPanel: function(dfn) {
        var items = ExtGeoNet.widget.Factory.createItems(ExtGeoNet.Main.metadata, dfn);
        var panel = {
            xtype: 'form',
            baseCls: 'x-plain',
            defaults: {
                anchor: "100%",
                style: 'margin-right:20px;'
            },
            items: items
        };

        if(items.length == 1) {
            if (items[0].fieldName === undefined) {
                panel.layout = 'fit';
                panel.xtype = 'panel';
            } else {
                items[0].anchor = '100% 50%';                
            }
        }

        return panel;
    }
};
