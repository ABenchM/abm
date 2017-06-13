angular.module('de.fraunhofer.abm').controller('modalLoginController', function($uibModalInstance, $rootScope, $scope, $http, $cookies) {
	var $ctrl = this;
	
	$scope.loading = false;
	$scope.credentials = {};
	$scope.alerts = [];

	$ctrl.login = function() {
		$scope.loading = true;
		$scope.alerts.clear();
		$http.post('/rest/login', $scope.credentials, null).then(
			function() {
				$rootScope.user = $scope.credentials.username;
				$cookies.put('user', $scope.credentials.username);
				$uibModalInstance.close();
			}, function(d) {
				if(d.status == 401) {
					$scope.alerts.push( { type: 'danger', msg: 'Wrong username or password' });
				} else {
					$scope.alerts.push( { type: 'danger', msg: 'Failed with ['+ d.status + '] '+ d.statusText });
				}
			}
		)['finally'](function() {
			$scope.loading = false;
		});
	};

	$ctrl.cancel = function() {
		$uibModalInstance.close();
	};
});