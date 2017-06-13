angular.module('de.fraunhofer.abm').controller('searchController', 
['$rootScope', '$scope', '$http', '$location', 'searchResultService', 'Notification',
function searchController($rootScope, $scope, $http, $location, searchResultService, Notification) {
	var self = this;

	self.results = searchResultService.results;
	
	self.buildsystem = {};
	self.buildsystems = [
		{name: 'Ant', code: 'ant'},
	    {name: 'Gradle', code: 'gradle'},
	    {name: 'Maven', code: 'maven'},
	    {name: 'sbt', code: 'sbt'}
    ];
	
	self.language = {};
	self.languages = [
		{name: 'C', code: 'c'},
		{name: 'C++', code: 'cpp'},
		{name: 'Groovy', code: 'groovy'},
		{name: 'HTML', code: 'html'},
		{name: 'Java', code: 'java'},
		{name: 'JavaScript', code: 'javascript'},
		{name: 'PHP', code: 'php'},
		{name: 'Python', code: 'python'},
		{name: 'Ruby', code: 'ruby'},
		{name: 'Scala', code: 'scala'},
		{name: 'XML', code: 'xml'}
    ];
	
	self.license = {};
	self.licenses = [
		{name: 'Apache License 2.0', code: 'APACHE2'},
		{name: 'BSD 2-Clause', code: 'BSD2CLAUSE'},
		{name: 'BSD 3-Clause', code: 'BSD3CLAUSE'},
		{name: 'Common Development and Distribution License (CDDL) 1.0', code: 'CDDL'},
		{name: 'Eclipse Public License', code: 'EPL'},
		{name: 'General Public License 2', code: 'GPL2'},
		{name: 'General Public License 3', code: 'GPL3'},
		{name: 'Lesser General Public License 2.1', code: 'LGPL21'},
		{name: 'Lesser General Public License 3', code: 'LGPL3'},
		{name: 'MIT', code: 'MIT'},
		{name: 'Mozilla Public License 1.1', code: 'MPL11'},
		{name: 'Mozilla Public License 2.0', code: 'MPL20'}
    ];
	
	self.size = {};
	self.sizes = [
		{name: '0 - 100', code: 0},
		{name: '100 - 1,000', code: 1},
		{name: '1,000 - 10,000', code: 2},
		{name: '> 10,000', code: 3}
    ];
	
	self.search = function() {
		$rootScope.loading = true;
		searchResultService.results.clear();
		$http({
			method: 'GET',
			url: '/rest/search/'+$scope.query,
			params: {language: self.language}
		}).then(
			function(d) {
				d.data.forEach(function(record) {
					searchResultService.results.push(record);
				});
			}, function(d) {
				if(d.status == '403') {
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