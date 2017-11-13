angular.module('de.fraunhofer.abm').controller('hermesViewerController', function($uibModalInstance, $rootScope, $scope, $http, $cookies, $location, $timeout,
		Notification, hermesViewerService) {
	var $ctrl = this;
	$ctrl.version = hermesViewerService.version;
	$ctrl.collection = hermesViewerService.collection;
	$scope.loading = false;
	$scope.resultList = [];
	
	$ctrl.loadResults = function(){
		
		$rootScope.loading = true;
		
		
	}
	
	$ctrl.close = function() {
	    
		$uibModalInstance.close();
	};
	
	
	
});