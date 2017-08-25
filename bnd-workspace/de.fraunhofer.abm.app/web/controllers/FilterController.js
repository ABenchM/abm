angular.module('de.fraunhofer.abm').controller("filterController",
['$rootScope', '$scope', '$http', '$location', '$route',
function filterController($rootScope, $scope, $http, $location, $route) {
	var self = this;
	$scope.result = undefined;
	
	self.loadResult = function(){
		
	}
	
	self.loadResult();
}]);