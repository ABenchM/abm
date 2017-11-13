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
		$http.post('/rest/username', $scope.request, null).then(
			function(d){
				if(d.data){
					$location.path('/registered');
				} else {
					Notification.error("This username has been taken. Please select a different one");
					self.available = false;
				}
			}, function(d){
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function() {
				$rootScope.loading = false
			});	
	};
}]);