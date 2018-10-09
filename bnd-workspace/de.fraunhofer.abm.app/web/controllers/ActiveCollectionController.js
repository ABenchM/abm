angular.module('de.fraunhofer.abm').controller('activecollectionController', 
[ '$rootScope', '$scope', '$http', 'Notification', '$location',
function($rootScope, $scope, $http, Notification, $location){
	var self = this;
	self.available = true;
	self.invalidEmail = false;
	
$scope.request = {};

self.activecollection = function(){
		$rootScope.loading = true;
                $scope.request.collectionname = "Test10";
                //The value for isActive should be true or false
                 $scope.request.isActive = "false"; 
		$http.post('/rest/activecollection', $scope.request, null).then(
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
