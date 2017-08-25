angular.module('de.fraunhofer.abm').controller('modalHermesController', function($uibModalInstance, $rootScope, $scope, $http, $cookies, $location, 
		Notification, modalHermesService) {
	var $ctrl = this;
	
	$scope.loading = false;
	$scope.filterList = [];
	$ctrl.filterDict = {};
	
	$ctrl.loadFilters = function() {
		$rootScope.loading = true;
		$http.get('/rest/filters').then(
			function success(d){
				$ctrl.filterDict = d.data;
				for(i=0;i<Object.keys($ctrl.filterDict).length;i++){
					filterName = Object.keys($ctrl.filterDict)[i];
					$scope.filterList.push({"name": filterName, "status": $ctrl.filterDict[filterName]});
				}
			}, function failure(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
		})['finally']( function(){
			$rootScope.loading = false;
		});
	}
	
	$ctrl.run = function(){
		for(i=0;i<$scope.filterList.length;i++){
			filter = $scope.filterList[i]
			$ctrl.filterDict[filter.name] = filter.status;
		}
		//TODO: Pass filters to back end here.
		modalHermesService.version.filtered = true; //TODO: Set this on the back end
		$uibModalInstance.close();
	}

	$ctrl.cancel = function() {
		$uibModalInstance.close();
	};
	
	$ctrl.loadFilters();
});