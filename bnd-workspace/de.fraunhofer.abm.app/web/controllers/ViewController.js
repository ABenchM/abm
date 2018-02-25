angular.module('de.fraunhofer.abm').controller("viewController", 
['$rootScope', '$scope', '$http', '$location', '$routeParams', 'Notification', 'ngCart', 'collectionService',
function viewController($rootScope, $scope, $http, $location, $routeParams, Notification, ngCart, collectionService) {
	var self = this;
	
	self.id = $routeParams.id;
	self.downloading = false;
	$scope.isFileExists = false;
	
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
	
		
	self.doesFileExist = function (urlToFile) {
	    var xhr = new XMLHttpRequest();
	    
	    xhr.open('HEAD', urlToFile, false);
	    xhr.send();
	     
	    if (xhr.status == "404") {
	        return false;
	    } else {
	        return true;
	    }
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
						
						result = self.doesFileExist('/download/' + self.buildResult.id);
						
						if(result==true)
						location.href = '/download/' + self.buildResult.id;
						else
						Notification.error("File does not exist");	
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
					result = self.doesFileExist('/downloadHermes/' + self.hermesResult.id);
					if(result==true){
					
						location.href = '/downloadHermes/' + self.hermesResult.id;
					}else{
						Notification.error("Hermes Results file does not exist");
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
	
	self.loadCollection(self.id);
}]);