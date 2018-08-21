angular.module('de.fraunhofer.abm').controller('registrationController', 
[ '$rootScope', '$scope', '$http', 'Notification', '$location',
function($rootScope, $scope, $http, Notification, $location){
	var self = this;
	self.available = true;
	self.invalidEmail = false;
	
	$scope.request = {};
	
	self.checkEmail = function(){
		if($scope.request.email == undefined){return;}
		splitEmail = $scope.request.email.split('@');
		if(splitEmail.length != 2){
			self.invalidEmail = true;
		} else if(splitEmail[0].length < 1 || splitEmail[1].length < 1){
			self.invalidEmail = true;
		} else if(splitEmail[1].indexOf('.') < 1 || splitEmail[1].lastIndexOf('.') == (splitEmail[1].length - 1)){
			self.invalidEmail = true;
		} else {
			self.invalidEmail = false;
		}
	}
	
	self.register = function(){
		$rootScope.loading = true;
		$scope.request.firstname = 'Firstname';
 		$scope.request.lastname = 'Lastname';
		$http.post('/rest/username', $scope.request, null).then(
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
