angular.module('de.fraunhofer.abm').controller("viewController", 
['$rootScope', '$scope', '$http', '$location', '$routeParams', 'Notification', 'ngCart', 'collectionService',
function viewController($rootScope, $scope, $http, $location, $routeParams, Notification, ngCart, collectionService ) {
	var self = this;
	 
	self.id = $routeParams.id;
	self.downloading = false;
	self.hermesResultExists = false;
	self.buildResultExists = false;
	
	self.loadCollection = function(collectionId){
		$rootScope.loading = true;
		$http({
		    method: 'GET',
			url: '/rest/collection',
			params: {'privateStatus': false, 'id': collectionId}
		}).then(
			function(resp) {
				if(resp.data[0] != undefined){
					$scope.collection = resp.data[0];
					$scope.selectedVersion = resp.data[0].versions[0];
					
					self.checkFile($scope.selectedVersion.id,'hermes');
					self.checkFile($scope.selectedVersion.id,'build');
					
					if($rootScope.user != undefined){
						$http({
							method: 'GET',
							url: '/rest/pin/' + $rootScope.user + '/'  + $scope.collection.id
						}).then(
							function success(d) {
								$scope.pinned = d.data;
							}, function failure(d){
									Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
							});
					}
				} else {
					Notification.error('Collection not found, collection may be private or may not exist.');
					$location.path('/');
				}
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				$location.path('/');
			})['finally'](function() {
				$rootScope.loading = false;
			});
	};
	
	$('body').tooltip({
	    selector: '[rel="tooltip"]'
	});
	
	
	self.checkFile = function(id,type){
		
		$http({
			method: 'GET',
			url: '/rest/fe/',
			params: {'id': id, 'type': type }
		}).then(
				function success(d) {
					
					if(type == 'hermes')
					self.hermesResultExists = d.data;
					else
					{
					self.buildResultExists = d.data;}	
								
				}, function failure(d){
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				});
					
				
		
	}

	

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
	
	self.downloadHermes = function(versionId){
	self.downloading = true;
	
	$http({
			method: 'GET',
			url: '/rest/instance/',
			params: {'id': versionId, 'privateStatus': false }
		}).then(
				function success(d) {
					self.hermesResult = d.data;
								
						location.href = '/downloadHermes/' + self.hermesResult.id;
			
					
					
				}, function failure(d){
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				})['finally'](function (){
					self.downloading = false;
				});
	
	};
	
	
	
	
	self.selectVersion = function(version){
		$scope.selectedVersion = version;
	}
	
	self.pin = function(){
		self.disabled = true;
		$http({
			method: 'POST',
			url: '/rest/pin/',
			data: {'type': "collection", 'user': $rootScope.user, 'id': $scope.collection.id}
		}).then(
			function(){
				$scope.pinned = true;
			}, function(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function (){
				self.disabled = false;
			});
	}
	
	self.unpin = function(){
		self.disabled = true;
		$http({
			method: 'DELETE',
			url: '/rest/pin/',
			data: {'type': "collection", 'user': $rootScope.user, 'id': $scope.collection.id}
		}).then(
			function(){
				$scope.pinned = false;
			}, function(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function (){
				self.disabled = false;
			});
	}
	
	self.selectAll = function(){
		for (var i=0; i<$scope.selectedVersion.commits.length; i++) {
			var repo = $scope.selectedVersion.commits[i].repository;
			ngCart.addItem(repo.id.toString(), repo.name, 1, 1, repo);
		}
	}
	
	self.removeAll = function(){
		for (var i=0; i<$scope.selectedVersion.commits.length; i++) {
			var repo = $scope.selectedVersion.commits[i].repository;
			ngCart.removeItemById(repo.id.toString());
		}
	}
	
	self.copy = function(){
		collectionService.toCreate = [];
		for(i=0;i<$scope.selectedVersion.commits.length; i++){
			collectionService.toCreate.push($scope.selectedVersion.commits[i].repository);
		}
		$location.path('/createCollection');
	}
	
	self.deletePublicCollection = function(){
		//self.disabled = true;
		$http({
			method: 'POST',
			url: '/rest/deletepubliccollection/',
			data: {'id': $scope.collection.id}
		}).then(
			function(){
				//$scope.pinned = true;
				console.log("Public collection is deleted");
			}, function(d){
				Notification.error('Deletion Failed with ['+ d.status+ '] '+ d.statusText);
			})['finally'](function (){
				//self.disabled = false;
			});
	}
	
	self.loadCollection(self.id);
}]);