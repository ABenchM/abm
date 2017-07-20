angular.module('de.fraunhofer.abm').controller('buildTabController', function($scope, $rootScope, $http, buildViewerService, Notification) {
	var self = this;
	
	self.tabs = buildViewerService.builds;
	self.isOpen = false;
	
	self.open = function(buildId) {
		buildViewerService.initialSelection = buildId;
		buildViewerService.launch();
		$rootScope.hideSidebar = true;
	};
	
	self.initilize = function(){
		buildViewerService.builds.splice(0, buildViewerService.builds.length);
		if($scope.user != undefined){
			$http({
				method: 'GET',
				url: '/rest/builds/' + $scope.user
			}).then(
				function success(d) {
					resp = d.data;
					for(i=0; i<resp.length; i++){
						buildViewerService.builds.push(resp[i]);
						self.addBuildListener(resp[i].id);
					}
				}, function failure(d) {
					if(d.status == 403) {
						modalLoginService();
					} else {
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				});
		}
	}
	
	self.addBuildListener = function(versionId){
		$http({
			method: 'GET',
			url: '/rest/build/' + versionId
		}).then(
				function success(d) {
					buildViewerService.addListener(d.data.id);
				}, function failure(d) {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				})
	}
	
	$rootScope.$watch('user', function() {
		self.initilize();
	})
});