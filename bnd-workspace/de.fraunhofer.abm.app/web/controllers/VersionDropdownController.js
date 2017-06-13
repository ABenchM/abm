angular.module('de.fraunhofer.abm').controller("versionDropdownController", 
['$rootScope', '$scope', '$log', '$location', '$http', '$route', '$routeParams', 'collectionService', 'Notification',
function versionDropdownController($rootScope, $scope, $log, $location, $http, $route, $routeParams, collectionService, Notification) {
	var self = this;
	self.collection = collectionService.getCollection();
	
	if(self.collection.id === undefined) {
		$rootScope.loading=true;
		$http({
		    method: 'GET',
			url: '/rest/collection/'+$routeParams.id
		}).then(
			function(resp) {
				var collection = resp.data;
				collectionService.setCollection(collection);
				$route.reload();
			}, function(d) {
				if(d.status == 401) {
					$location.path('/login');
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					$location.path('/');
				}
			})['finally'](function() {
				$rootScope.loading=false;
			});
		
		// since, the collection has to be loaded first, we stop the current method call
		// the http request then reloads the route when the data is available
		return;
	}

	$scope.items = [];
	for(var i = 0; i<self.collection.versions.length; i++) {
		$scope.items.push(self.collection.versions[i]);
	}

	$scope.status = {
		isopen : false
	};

	$scope.toggled = function(open) {
		//$log.log('Dropdown is now: ', open);
	};

	$scope.toggleDropdown = function($event) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.status.isopen = !$scope.status.isopen;
	};
	
	$scope.selectVersion = function(version) {
		collectionService.version = version;
		$route.reload();
	}
	
	$scope.deriveVersion = function() {
		$scope.disabled=true;
		$http({
		    method: 'POST',
			url: '/rest/version/derive',
			data: collectionService.version
		}).then(
			function(resp) {
				var derivedVersion = resp.data;
				collectionService.collection.versions.push(derivedVersion);
				collectionService.collection.version = derivedVersion;
				$scope.selectVersion(derivedVersion);
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function() {
				$scope.disabled=false;
			});
	}
	
	$scope.unfreeze = function(version) {
		$scope.disabled=true;
		$http({
		    method: 'POST',
			url: '/rest/version/unfreeze',
			data: version
		}).then(
			function(resp) {
				collectionService.version = version;
				collectionService.collection.version = version;
				collectionService.collection.version.frozen = false;
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function() {
				$scope.disabled=false;
			});
	}
	
	$scope.showBuild = function(version) {
		$location.path('/build/'+version.id);
	}
}]);