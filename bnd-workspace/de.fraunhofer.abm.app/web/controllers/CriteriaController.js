angular.module('de.fraunhofer.abm').controller("criteriaController", 
['$rootScope', '$scope', '$http', '$location', '$route', 'ngCart', 'modalLoginService', 'collectionService', 'Notification',
function criteriaController($rootScope, $scope, $http, $location, $route, ngCart, modalLoginService, collectionService, Notification) {
	var self = this;
	self.collection = collectionService.collection;

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

	function createRequestObject() {
		return {
			criteria : {
				size : [],
				buildsystems : [],
				languages : [],
				licenses : [],
				sizes : []
			},
			repos : []
		};
	}

	function addRepositoriesToRequest(request) {
		items = ngCart.getItems();
		for(var i=0; i<items.length; i++) {
			item = items[i];
			request.repos.push(item._data);
		}
	}
	
	function addLicensesToRequest(request) {
		if(self.license.selected) {
			var licenseList = [];
			var selectedLicenses = self.license.selected;
			for (var i = 0; i < selectedLicenses.length; i++) {
				var license = selectedLicenses[i];
				licenseList.push(license.code);
			}
			request.criteria.licenses = licenseList;
		}
	}
	
	function addLanguagesToRequest(request) {
		if(self.language.selected) {
			var languageList = [];
			var selectedLanguages = self.language.selected;
			for (var i = 0; i < selectedLanguages.length; i++) {
				var language = selectedLanguages[i];
				languageList.push(language.code);
			}
			request.criteria.languages = languageList;
		}
	}
	
	function addBuildSystemsToRequest(request) {
		if(self.buildsystem.selected) {
			var buildsystemList = [];
			var selectedBuildSystems = self.buildsystem.selected;
			for (var i = 0; i < selectedBuildSystems.length; i++) {
				var buildsystem = selectedBuildSystems[i];
				buildsystemList.push(buildsystem.code);
			}
			request.criteria.buildsystems = buildsystemList;
		}
	}
	
	function addSizesToRequest(request) {
		if(self.size.selected) {
			var sizeList = [];
			var selectedSizes = self.size.selected;
			for (var i = 0; i < selectedSizes.length; i++) {
				var size = selectedSizes[i];
				sizeList.push(size.code);
			}
			request.criteria.sizes = sizeList;
		}
	}
	
	self.start = function() {
		var request = createRequestObject();
		addRepositoriesToRequest(request);
		addBuildSystemsToRequest(request);
		addLicensesToRequest(request);
		addLanguagesToRequest(request);
		addSizesToRequest(request);

		$rootScope.loading = true;
		self.disabled = true;
		$http({
		    method: 'POST',
			url: '/rest/criteria',
			data: request
		}).then(
			function(resp) {
				if(resp.status == 200) {
					matches = resp.data;
					removeIds = [];
					for(var i=0; i<items.length; i++) {
						found = false;
						item = items[i];
						for(var j=0; j<matches.length; j++) {
							match = matches[j];
							if(match.id == item._data.id) {
								found = true;
								break;
							}
						}
						if(!found) {
							removeIds.push(item._id);
						}
					}
					
					for(var i=0; i<removeIds.length; i++) {
						ngCart.removeItemById(removeIds[i]);
					}
					
					$location.path('/collection');
					Notification.success(removeIds.length + ' projects have been removed from your selection.');
				}
			}, 
			function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			}
		)['finally'](function() {
			$rootScope.loading = false;
			self.disabled = false;
		});
		
	};
	
}]);