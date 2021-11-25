(function () {

  goog.provide('gn_search_georchestra');
  goog.require('gn_search_default');

  var module = angular.module('gn_search_georchestra',
    ['gn_search_default']);

  // Add a custom georchestra locale file
  module.config(['$LOCALES', function ($LOCALES) {
    $LOCALES.push('georchestra');
  }]);

  /**
   * Overload default settings
   */
  module.run([
    'gnSearchSettings',
    'gnExternalViewer',
    'gnGlobalSettings',
    '$filter',
    'gnLangs',
    '$window',
    'gnHttp',
    '$location',
    'gnAlertService',
    function (gnSearchSettings, gnExternalViewer, gnGlobalSettings, $filter, gnLangs, $window, gnHttp, $location, gnAlertService) {

      var settings = gnGlobalSettings.gnCfg.mods.map.externalViewer;
      var baseMdUrl = $location.absUrl().split('#')[0] + '#/metadata/';

      gnSearchSettings.linkTypes = {
        links: ['LINK'],
        downloads: ['DOWNLOAD'],
        layers: ['OGC:WMS'],
        maps: ['ows']
      };

      // Add custom actions in selection action button
      gnSearchSettings.customSelectActions = [{
        icon: 'fa-globe',
        fn: function(id) {
          gnHttp.callService('selectionLayers', {
            id: id && angular.isString(id) ? id : undefined
          }).then(function(response) {
            var wmcCount = 0;
            var wmsCount = 0;

            var processEntries = function (item) {
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
            response.data.services.map(processEntries);
            response.data.layers.map(processEntries);

            if (wmsCount == 0) {
              gnAlertService.addAlert({
                msg: 'invalidSelectionZeroWMS',
                type: 'danger'
              });
              return;
            }

            response.data.layers.filter(function (layer) {
              return layer.owstype === 'WMS'
            }).forEach(function (layer) {
              var service = {
                name: layer.layername,
                url: layer.owsurl,
                type: layer.owstype
              };
              var md = {
                uui: layer.muuid
              };
              gnExternalViewer.viewService(md, service);
            });
            gnExternalViewer._commit();
          });
        },
        label: 'viewLayers'
      }];

      gnExternalViewer._commit = function () {
        if (!this._toView.length) {
          return;
        }

        var layers = this._toView.map(function (toView) {
          var name = toView.service.name;
          return encodeURIComponent($filter('gnLocalized')(name) || name || '');
        }).map(function(name) { return '"' + name + '"' }) || '';

        var services = this._toView.map(function (toView) {
          var url = toView.service.url;
          return JSON.stringify({
            type: toView.service.type,
            url: encodeURIComponent($filter('gnLocalized')(url) || url || '')
          })
        }).join(settings.valueSaparator || ',');

        var url = settings.urlTemplate
          .replace('${service.url}', services)
          .replace('${service.name}', layers)

        settings.openNewWindow ? $window.open(url, '_blank') :
          $window.location = url;

        // reset list of services to view
        this._toView.length = 0;
      }.bind(gnExternalViewer);
    }]);
})()
