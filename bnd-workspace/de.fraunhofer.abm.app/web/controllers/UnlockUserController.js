angular.module('de.fraunhofer.abm').controller('unlockuserController', 
[ '$rootScope', '$scope', '$http', 'Notification', '$location',
function($rootScope, $scope, $http, Notification, $location){
	var self = this;
	self.available = true;
	self.invalidEmail = false;
	
$scope.request = {};

self.lockaccount = function(){
		$rootScope.loading = true;
                $scope.request.username = "anitha";
                //The value for isLock should be true or false
                 $scope.request.isLock = "true"; 
		$http.post('/rest/userlockunlock', $scope.request, null).then(
			function(d){
				if(d.data){
					$location.path('/registered');
				} else {
					Notification.error("This username has been taken. Please select a different one");
					self.available = false;
				}
			}, function(d){
				Notification.error('Internal error: registrations cannot be done at the moment. Please try agin later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
			})['finally'](function() {
				$rootScope.loading = false
			});	
	};
}]);
