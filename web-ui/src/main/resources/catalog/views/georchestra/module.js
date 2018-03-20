(function() {

  goog.provide('gn_search_georchestra');
  goog.require('gn_search_default');
  goog.require('georchestra_linkactions');

  var module = angular.module('gn_search_georchestra',
      ['gn_search_default', 'georchestra_linkactions']);


  // Add a custom georchestra locale file
  module.config(['$LOCALES', function($LOCALES) {
    $LOCALES.push('georchestra');
  }]);

  /**
   * Overload default settings
   */
  module.run([
    'gnSearchSettings',
    'georLinkActionsService',

    function(gnSearchSettings, georLinkActionsService) {
      gnSearchSettings.linkTypes = {
        links: ['LINK'],
        downloads: ['DOWNLOAD'],
        layers:['OGC', 'vnd.ogc.wms_xml'],
        maps: ['ows']
      };

      // Add custom actions in selection action button
      gnSearchSettings.customSelectActions = [{
        icon: 'fa-globe',
        fn: function() {
          georLinkActionsService.extractMetadata('mapfishapp');
        },
        label: 'viewLayers'
      }, {
        icon: 'fa-eject',
        fn: function() {
          georLinkActionsService.extractMetadata('mapfishapp/?addons=extractor_0');
        },
        label: 'extractLayers'
      }];
    }]);


  /**
   * ExtendMainController is a constructor of a new controller that is here
   * to extend the `gnsDefault` controller, containing main logic of the
   * default search page.
   *
   * @param {Object} $scope is shared with gnsDefault scope
   * @param {Object} georLinkActionsService
   * @constructor
   */
  var ExtendMainController = function($scope, georLinkActionsService, gnRelatedResources) {
    if($scope.resultviewFns) {

      // Overrides add wms to map buttons action in main search scope
      $scope.resultviewFns.addMdLayerToMap = georLinkActionsService.addWMSLayer;
      $scope.resultviewFns.addAllMdLayersToMap = function(layers, md) {
        georLinkActionsService.extractMetadata('mapfishapp', md.getId());
      }
    }
    gnRelatedResources.configure({
        'WMS' : {
          iconClass: 'fa-globe',
          label: 'addToMap',
          action: georLinkActionsService.addWMSLayer
        }
     });
  };

  module.directive('gnExtendMainctrl', [
    function() {
      return {
        restrict: 'E',
        scope: false,
        controller: ExtendMainController
      };
    }
  ]);

  ExtendMainController['$inject'] = [
    '$scope',
    'georLinkActionsService',
    'gnRelatedResources'
  ];

})();
