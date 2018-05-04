(function() {

  goog.provide('georchestra_linkactions');

  var module = angular.module('georchestra_linkactions', []);

  /**
   * @ngdoc service
   * @kind function
   * @name georchestra_linkactions.service:georLinkActionsService
   *
   * @description
   * The `georLinkActionsService` service provides method to export resources
   * of the metadata into georchestra applications (mapfishapp, extractorapp).
   *
   */
  module.service('georLinkActionsService', [
    'gnHttp',
    'gnAlertService',
    '$location',
    'gnMap',
      function(gnHttp, gnAlertService, $location, gnMap) {

        /**
         * Url pattern for metadata page
         * @type {string}
         */
        var baseMdUrl = $location.absUrl().split('#')[0] + '#/metadata/';

        /**
         * Send WMS layers to mapfishapp
         *
         * @param {Array.<Object>} layers
         * @property {Object} layers[].link the linked resource
         * @property {Object} layers[].md the metadata object
         */
        this.addWMSLayers = function (layers) {
          var jsonObject = {services: [], layers: []};
          layers.forEach(function(layer) {
            jsonObject.layers.push(this.getLayerJSONSpec(layer.link, layer.md));
          }.bind(this));
          sendPostForm('mapfishapp', JSON.stringify(jsonObject));
        };

        /**
         * Send a WMS layer to mapfishapp
         *
         * @param {Object} link the linked resource
         * @param {Object} md the metadata object
         */
        this.addWMSLayer = function (link, md) {
          var jsonObject = {services: [], layers: []};
          jsonObject.layers.push(this.getLayerJSONSpec(link, md));
          sendPostForm('mapfishapp', JSON.stringify(jsonObject));
        };

        /**
         * Get JSON spec to send to mapfishapp
         *
         * @param {Object} link the linked resource
         * @param {Object} md the metadata object
         */
        this.getLayerJSONSpec = function(link, md) {
          var metadataUrl = baseMdUrl + md.getUuid();
          var layerConfig = gnMap.getLayerConfigFromLink(link);
          var json;

          // If !link.name then it's a service
          if(layerConfig.name) {
            json = {
              layername: layerConfig.name,
              metadataURL: metadataUrl,
              owstype: 'WMS',
              owsurl: layerConfig.url,
              title: layerConfig.desc
            };
          } else {
            json = {
              metadataURL: metadataUrl,
              owstype: 'WMS',
              owsurl: layerConfig.url,
              title: layerConfig.desc
            };
          }
          return json;
        }

        /**
         * @ngdoc method
         * @name georLinkActionsService#extractMetadata
         * @methodOf georchestra_linkactions.service:georLinkActionsService
         *
         * @description
         * extractMetadata will call a GN internal service metadata.service.extract to retrieve all WMS layers or services
         * to be send to mapfishapp or extractorapp.
         * The service will look into all selected layers if id is null (called from other actions menus).
         * The service will look into one MD with a given ID if id is not null (right click on metadata in MetadataResultsView).
         *
         * All layers and services contained by the target metadata will be send into a JSON to mapfishapp or extractorapp
         * @note : metadata.service.extract doesnt manage WMC.
         *
         * @param {string} destUrl application to send the datas to
         * @param {string} id id of the MD to look into (optionnal).
         *        If id is null, service look into all selected MD
         */
        this.extractMetadata = function(destUrl, id) {

          gnHttp.callService('selectionLayers', {
            id: id && angular.isString(id) ? id : undefined
          }).then(function(response) {
            var wmcCount = 0;
            var wmsCount = 0;

            var processEntries = function(item) {
              item.metadataURL = baseMdUrl + item.muuid;
              switch (item.owstype) {
                case 'WMC':
                  wmcCount += 1;
                  break;

                case 'WMS':
                  wmsCount += 1;
                  break;
              }
            };

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
            response.data.services.map(processEntries);
            response.data.layers.map(processEntries);

            /* Checking inputs - rule #1 */
            if (wmcCount > 1) {
              gnAlertService.addAlert({
                msg: 'invalidSelectionMoreThanOneWMC',
                type: 'danger'
              });
              return;
            }
            /* rule #2 */
            if ((wmcCount > 0) && (wmsCount > 0)) {
              gnAlertService.addAlert({
                msg: 'invalidSelectionOneWMCandOneOrMoreWMS',
                type: 'danger'
              });
              return;
            }
            /* new rule : No data (no WMS nor WMC) available into
             * selected MDs. Alerting the user
             */
            if ((wmcCount == 0) && (wmsCount == 0)) {
              gnAlertService.addAlert({
                msg: 'invalidSelectionnoWMCnorWMS',
                type: 'danger'
              });
              return;
            }
            sendPostForm(destUrl, JSON.stringify(response.data));
          });
        };

        /**
         * Submit a post form to load remote application with form datas.
         * This is the way we open mapfishapp and extractorapp with predfined
         * layers, services, or contexts.
         *
         * @param {string} destUrl
         * @param {string} content
         */
        function sendPostForm(destUrl, content) {
          var tmpForm = $('<form action="/' + destUrl + '/" method="POST" ' +
              'target="_blank"></form>');
          var tmpInput = $('<input name="data" />');
          tmpForm.appendTo('body');
          tmpForm.append(tmpInput);

          tmpInput.val(content);
          tmpForm.submit();
          tmpForm.remove();
        };
      }
  ])
})();
