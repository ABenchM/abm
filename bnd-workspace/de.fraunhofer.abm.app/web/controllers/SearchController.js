angular.module('de.fraunhofer.abm').controller('searchController', 
['$rootScope', '$scope', '$http', '$location', 'searchResultService', 'Notification',
function searchController($rootScope, $scope, $http, $location, searchResultService, Notification) {
	var self = this;

	self.results = searchResultService.results;
	
	self.search = function() {
		$rootScope.loading = true;
		searchResultService.results.clear();
		$http.get('/rest/search/'+$scope.query).then(
			function(d) {
				d.data.forEach(function(record) {
					searchResultService.results.push(record);
				});
			}, function(d) {
				if(d.status == 401) {
					$location.path('/login');
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				}
			}
		)['finally'](function() {
			$rootScope.loading = false;
		});
	};
}]);