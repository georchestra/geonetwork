(function() {

  goog.provide('gn_search_georchestra');
  goog.require('gn_search_default');
  goog.require('georchestra_linkactions');

  var module = angular.module('gn_search_georchestra',
      ['gn_search_default', 'georchestra_linkactions']);

  /**
   * Overload default settings
   */
  module.run(['gnSearchSettings', 'georLinkActionsService',
    function(gnSearchSettings, georLinkActionsService) {

    gnSearchSettings.linkTypes = {
      links: ['LINK'],
      downloads: ['DOWNLOAD'],
      layers:['OGC', 'vnd.ogc.wms_xml'],
      maps: ['ows']
    };

    gnSearchSettings.customSelectActions = [{
      icon: 'fa-globe',
      fn: function() {
        georLinkActionsService.extractMetadata('mapfishapp');
      },
      label: 'viewLayers'
    }, {
      icon: 'fa-eject',
      fn: function() {
        georLinkActionsService.extractMetadata('extractLayers');
      },
      label: 'viewLayers'
    }]
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
  var ExtendMainController = function($scope, georLinkActionsService) {
    if($scope.resultviewFns) {

      $scope.resultviewFns.addMdLayerToMap = georLinkActionsService.addWMSLayer;
      $scope.resultviewFns.addAllMdLayersToMap = function(layers, md) {
        georLinkActionsService.extractMetadata('mapfishapp', md.getId());
      }
    }
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
    'georLinkActionsService'
  ];

})();
