angular.module('de.fraunhofer.abm').controller('modalHermesController', function($uibModalInstance, $rootScope, $scope, $http, $cookies, $location, $timeout,
		Notification, modalHermesService) {
	var $ctrl = this;
	$ctrl.version = modalHermesService.version;
	$ctrl.collection = modalHermesService.collection;
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
		/*    function(d){
		    var hermesResultId = d.data;
		    hermesViewerService.steps.push({"id": $ctrl.version.id, "name": $ctrl.collection.name, "versionNum": $ctrl.version.number, "progress": 0, "hermesStatus": 'RUNNING', "hidden": false});
		    targetTab = hermesViewerService.steps.length - 1;
		    $ctrl.getHermesProgress($ctrl.version.id, targetTab);
		    } , function(d) {
					if(d.status == 403) {
						modalLoginService();
					} else {
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				})['finally'](function() {
					$rootScope.loading = false;*/
			function success(d){
				Notification.success('Hermes has been started');
			}, function failure(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally']( function(){
				$rootScope.loading = false;
				$uibModalInstance.close();
				$ctrl.poller();
		});
	}

	$ctrl.cancel = function() {
	    
		$uibModalInstance.close();
	};
	
	$ctrl.poller = function(){
	
	$http({
			method: 'GET',
			url: '/rest/instance/' + $ctrl.version.id			
		}).then(
			function(d) {
			
			$rootScope.hermesStatus = d.data.status;
			if(d.data.status=='FINISHED'){
			     $timeout.cancel($ctrl.poller);}
			else{     
			$timeout($ctrl.poller,8000);
			}
			});
	
	
	}
	
		
	$ctrl.loadFilters();
});