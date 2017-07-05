angular.module('de.fraunhofer.abm').controller("publicController", 
['$rootScope', '$scope', '$http', '$location', '$route', 'Notification', 'publicCollectionService', 'modalLoginService',
function publicController($rootScope, $scope, $http, $location, $route, Notification, publicCollectionService, modalLoginService) {
	var self = this;
		
	self.collections = publicCollectionService.collections;
	self.disabled = false;
	$scope.pinned = {};
		
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
			})['finally'](function (){
				if($rootScope.user == undefined){
					self.loading = false;
				} else {
					for(i=0;i<$scope.publicData.length;i++){
						self.checkPinned($scope.publicData[i]);
					}
					$http({
					    method: 'GET',
						url: '/rest/pin/',
						params: {'type': "collection", 'user': $rootScope.user}
					}).then(
						function(resp) {
							$scope.pinned = resp.data;
						}, function(d) {
							if(d.status == '403'){
								Notification.error('You may have been logged out due to unexpected maintenance. Please try logging back in to re-enable all features.');
								modalLoginService();
							} else {
								Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
							}
						})['finally'](function (){
							self.loading = false;
						});
				}
			});
	}


	
	self.pin = function(target){
		self.disabled = true;
		$http({
			method: 'POST',
			url: '/rest/pin/',
			data: {'type': "collection", 'user': $rootScope.user, 'id': target.id}
		}).then(
			function(){
				$scope.pinned.push(target);
				targetIndex = $scope.publicData.findIndex(self.checkId, target.id);
				$scope.publicData.splice(targetIndex, 1);
			}, function(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function (){
				self.disabled = false;
			});
	}
	
	self.unpin = function(target){
		self.disabled = true;
		$http({
			method: 'DELETE',
			url: '/rest/pin/',
			data: {'type': "collection", 'user': $rootScope.user, 'id': target.id}
		}).then(
			function(){
				pinnedIndex = $scope.pinned.findIndex(self.checkId, target.id);
				publicIndex = $scope.publicData.findIndex(self.checkId, target.id);
				$scope.pinned.splice(pinnedIndex, 1);
				$scope.publicData[publicIndex].pinned = false;
			}, function(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function (){
				self.disabled = false;
			});
	}
	
	self.checkPinned = function(item){
		$http({
			method: 'GET',
			url: '/rest/pin/' + $rootScope.user + '/'  + item.id
		}).then(
			function success(d) {
				item.pinned =  d.data;
			}, function failure(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			});
	}
	
	self.view = function(id) {
		$location.path('/view/' + id);
	}
	
	self.checkId = function(item){
		return (item.id == this);
	}
	
	self.initilize();

}]);