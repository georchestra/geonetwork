Ext.namespace('GeoNetwork');

/**
 * Namespace GeoNetwork.MapTools
 * 
 * Offers several static functions to manage the communication between GN and mapfishapp/extractorapp.
 * This functions are shared by all georchestra UI so shouldnt be in config app.js
 */
GeoNetwork.MapTools = function() {
    
    return {
        
        addWMC: function(url) {
            window.open('/mapfishapp/?wmc=' + url);
        },
        
        /**
         * This method send a single WMS layer to mapfishapp.
         * It is called on user left click on the layer (in MetadataResultsView and in the MD view).
         * This create a JSON object with all the layer param to be send to mapfishapp
         * 
         * @params : args is an array of the layer parameter [title,url,name,mdId]
         */
        addWMSLayer: function (args) {
            var layer = args[0];
            
            // Send layers (no service) to mapfishapp
            var jsonObject = {services: [], layers: []};
            
            if (layer[2]) {
                jsonObject.layers.push({
                    layername: layer[2],
                    metadataURL: app.getCatalogue().URL + '?uuid=' + layer[3],
                    owstype: 'WMS',
                    owsurl: layer[1],
                    title: layer[0]
                });
            } else {
                jsonObject.services.push({
                    metadataURL: app.getCatalogue().URL + '?uuid=' + layer[3],
                    owstype: 'WMS',
                    owsurl: layer[1],
                    title: layer[0]
               });
            }

            var form = Ext.DomHelper.append(Ext.getBody(), {
              tag: 'form',
              action: '/mapfishapp/',
              target: "_blank",
              method: 'post'
            });

            var input = Ext.DomHelper.append(form, {
              tag: 'input',
              name: 'data'
            });

            input.value = new OpenLayers.Format.JSON().write(jsonObject);
            form.submit();
            Ext.removeNode(form);
        },

        /**
         * extractMetadata will call a GN internal service metadata.service.extract to retrieve all WMS layers or services
         * to be send to mapfishapp or extractorapp. 
         * The service will look into all selected layers if id is null (called from other actions menus).
         * The service will look into one MD with a given ID if id is not null (right click on metadata in MetadataResultsView).
         * 
         * All layers and services contained by the target metadata will be send into a JSON to mapfishapp or extractorapp
         * @note : metadata.service.extract doesnt manage WMC.
         * 
         * @params: id of the MD to look into (optionnal). If id is null, service look into all selected MD
         */
        extractMetadata: function(destUrl, id) {
            var urlService = catalogue.services.mdExtract;
            if(id && typeof(id) == 'string') {
                urlService += '?id='+id
            }
            Ext.Ajax.request({
               url: urlService,
               success: function(req) {
                      var jsFromXml = req.responseXML || new OpenLayers.Format.XML().read(req.responseText);
                      var jsonObject = {services: [], layers: []};
                      /*
                       * Implementing rules from the wiki :
                       *
                       *  1. if multiple WMC docs are selected in
                       *  GeoNetwork the latter will refuse to open the MapFish app
                       *
                       *  2. if a WMC doc and WMS items (layers or services) are selected
                       *  in GeoNetwork the latter will refuse to open the MapFish app
                       *
                       *  3. if WMS services are selected the MapFish app will open a dialog
                       *  window for the user to select layers
                       *
                       */
                      var wmcCount = 0;
                      var wmsCount = 0;
                      Ext.each(jsFromXml.getElementsByTagName('service'), function(item, index, array) {
                          var owsType = item.getAttribute('owstype');
                          jsonObject.services.push({
                            text: item.getAttribute('text'),
                            metadataURL: app.getCatalogue().services.mdShow + '?id=' + item.getAttribute('mdid'),
                            owstype: owsType,
                            owsurl: item.getAttribute('owsurl')
                          });

                          switch (owsType) {
                            case 'WMC':
                              wmcCount += 1;
                              break;

                            case 'WMS':
                              wmsCount += 1;
                              break;
                          }
                      });

                      Ext.each(jsFromXml.getElementsByTagName('layer'), function(item, index, array) {
                        var owsType = item.getAttribute('owstype');

                        jsonObject.layers.push({
                          layername: item.getAttribute('layername'),
                          metadataURL: app.getCatalogue().services.mdShow + '?id=' + item.getAttribute('mdid'),
                          owstype: owsType,
                          owsurl: item.getAttribute('owsurl')
                        });

                        switch (owsType) {
                          case 'WMC':
                            wmcCount += 1;
                            break;
                          case 'WMS':
                            wmsCount += 1;
                            break;
                        }
                      });

                      /* Checking inputs - rule #1 */
                      if (wmcCount > 1) {
                        alert(OpenLayers.i18n("invalidSelectionMoreThanOneWMC"));
                        return;
                      }
                      /* rule #2 */
                      if ((wmcCount > 0) && (wmsCount > 0)) {
                        alert(OpenLayers.i18n("invalidSelectionOneWMCandOneOrMoreWMS"));
                       return;
                      }
                      /* new rule : No data (no WMS nor WMC) available into
                       * selected MDs. Alerting the user
                       */
                      if ((wmcCount == 0) && (wmsCount == 0)) {
                        alert(OpenLayers.i18n("invalidSelectionnoWMCnorWMS"));
                        return;
                      }

                      var form = Ext.DomHelper.append(Ext.getBody(), {
                        tag: 'form',
                        action: destUrl,
                        target: "_blank",
                        method: 'post'
                      });

                      var input = Ext.DomHelper.append(form, {
                        tag: 'input',
                        name: 'data'
                      });

                      input.value = new OpenLayers.Format.JSON().write(jsonObject);
                      form.submit();
                      Ext.removeNode(form);
                    }
            });
        },
        
        /**
         * addExtractActions create 2 actions to visualize and extract WMS data and add those actions
         * into MetadataMenu and ViewPanel menu.
         * 
         * @note : OtherActions menu is managed differently
         * 
         * This method is respectively called into the MetadataMenu & ViewPanel objects themselves. 
         * The method is passed as a config param on object creation, then called inside the objects.
         * 
         * The actions will be added into the metadata menu, if the MD contains no WMS references, then
         * the actions will be disabled in the menu.
         * 
         */
        addExtractActions: function() {
            
            var urlVisu = '/mapfishapp/';
            var urlExtract = '/extractorapp/';
            
            // Check if we have WMS layers or service to send to mapfishapp or extractorapp
            var hasWMS = function(record) {
                var links = record.get('links');
                for(i=0;i<links.length;i++) {
                    if(links[i].protocol.indexOf('WMS') >= 0 ){
                        return false;
                    }
                }
                return true;
            };
            
            // 'this' reference the MetadataMenu
            var extractAction = new Ext.Action({
                text: OpenLayers.i18n('extractData'),
                disabled: hasWMS(this.record),
                handler: function() {
                    app.extractMetadata(urlExtract, this.record.get('id'));
                },
                scope: this
            });
            var visuAction = new Ext.Action({
                text: OpenLayers.i18n('visualizeData'),
                disabled: hasWMS(this.record),
                handler: function() {
                    app.extractMetadata(urlVisu, this.record.get('id'));
                },
                scope: this
            });
            this.insert(0,visuAction);
            this.insert(0,extractAction);
        }
    }
}();