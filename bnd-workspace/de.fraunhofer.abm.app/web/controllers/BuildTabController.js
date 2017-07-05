angular.module('de.fraunhofer.abm').controller('buildTabController', function($scope, $rootScope, $http, buildViewerService, Notification) {
	var self = this;
	
	self.tabs = buildViewerService.builds;
	
	self.open = function(buildId) {
		buildViewerService.initialSelection = buildId;
		buildViewerService.launch();
		$rootScope.hideSidebar = true;
	};
});