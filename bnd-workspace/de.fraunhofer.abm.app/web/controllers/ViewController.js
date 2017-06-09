angular.module('de.fraunhofer.abm').controller("viewController", 
['$rootScope', '$scope', '$http', '$location', '$routeParams', 'Notification',
function viewController($rootScope, $scope, $http, $location, $routeParams, Notification) {
	var self = this;
	
	self.id = $routeParams.id;
	self.downloading = false;
	
	self.loadCollection = function(collectionId){
		$rootScope.loading = true;
		$http({
		    method: 'GET',
			url: '/rest/collection',
			params: {'privateStatus': false, 'id': collectionId}
		}).then(
			function(resp) {
				$scope.collection = resp.data[0];
				$scope.selectedVersion = resp.data[0].versions[0];
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				$location.path('/');
			})['finally'](function() {
				$rootScope.loading = false;
			});
	};
	
	self.downloadBuild = function(versionId){
		self.downloading = true;
		$http({
			method: 'GET',
			url: '/rest/build/',
			params: {'id': versionId, 'privateStatus': false}
		}).then(
				function success(d) {
					self.buildResult = d.data;
					if(self.buildResult.status == 'RUNNING'){
						Notification.error('Build is in progress, try again later');
					} else {
						location.href = '/download/' + self.buildResult.id;
					}
				}, function failure(d){
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				})['finally'](function (){
					self.downloading = false;
				});
	};
	
	self.selectVersion = function(version){
		$scope.selectedVersion = version;
	}
	
	self.loadCollection(self.id);
}]);