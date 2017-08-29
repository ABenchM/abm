angular.module('de.fraunhofer.abm').controller('modalHermesController', function($uibModalInstance, $rootScope, $scope, $http, $cookies, $location, 
		Notification, modalHermesService) {
	var $ctrl = this;
	
	$scope.loading = false;
	$scope.filterList = [];
	
	$ctrl.loadFilters = function() {
		$rootScope.loading = true;
		$http.get('/rest/activeFilters/' + modalHermesService.version.id).then(
			function success(d){
				$scope.filterList = d.data;
			}, function failure(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
		})['finally']( function(){
			$rootScope.loading = false;
		});
	}
	
	$ctrl.run = function(){
		$rootScope.loading = true;
		$http.post('/rest/hermes/' + modalHermesService.version.id, $scope.filterList).then(
			function success(d){
				Notification.success('Hermes has been started');
			}, function failure(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally']( function(){
				$rootScope.loading = false;
				$uibModalInstance.close();
		});
	}

	$ctrl.cancel = function() {
		$uibModalInstance.close();
	};
	
	$ctrl.loadFilters();
});