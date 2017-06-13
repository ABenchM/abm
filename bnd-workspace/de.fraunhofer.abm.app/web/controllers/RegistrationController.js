angular.module('de.fraunhofer.abm').controller('registrationController', 
[ '$rootScope', '$scope', '$http', 'Notification',
function($rootScope, $scope, $http, Notification){
	$scope.request = {};
	$scope.register = function(){
		Notification.info('Registration is currently closed.');
	};
}]);