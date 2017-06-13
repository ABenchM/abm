angular.module('de.fraunhofer.abm').controller("publicController", 
['$rootScope', '$scope', '$http', '$location', '$route', 'Notification', 'publicCollectionService',
function publicController($rootScope, $scope, $http, $location, $route, Notification, publicCollectionService) {
	var self = this;
		
	this.collections = publicCollectionService.collections;
		
	self.initilize = function() {
		self.loading = true;
		$http({
		    method: 'GET',
			url: '/rest/collection',
			params: {'privateStatus': false}
		}).then(
			function(resp) {
				$scope.publicData = resp.data;
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function() {
				self.loading = false;
			});
	}
	
	self.view = function(id) {
		$location.path('/view/' + id);
	}
	
	self.initilize();

}]);