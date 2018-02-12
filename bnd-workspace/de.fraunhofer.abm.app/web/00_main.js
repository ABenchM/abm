// this is named 00_main, so that the enroute webresource servlet copies tthis code infront of all other scripts
'use strict';

(function() {
	var MODULE = angular.module('de.fraunhofer.abm',
			[ 'ngRoute', 'ngResource', 'ngCookies', // standard angular modules 
			  'ngCart',                // used to provide the shopping cart like functionality to create a new suite
			  'ui.grid', 'ui.grid.resizeColumns', 'ui.grid.autoResize',
			  'ui.bootstrap',
			  'ngSanitize', 'ui.select', 
			  'angular-confirm',
			  'ui-notification',
			  'google-signin',
			  'ngAnimate'
			]);

	MODULE.config(['$routeProvider', 'GoogleSigninProvider', function($routeProvider, GoogleSigninProvider) {
		$routeProvider.when('/', { controller: mainProvider, templateUrl: '/de.fraunhofer.abm/template/home.htm'});
		$routeProvider.when('/search', { controller: mainProvider, templateUrl: '/de.fraunhofer.abm/template/search.htm'});
		$routeProvider.when('/about', { templateUrl: '/de.fraunhofer.abm/template/about.htm'});
		$routeProvider.when('/collection', { templateUrl: '/de.fraunhofer.abm/template/cart.htm'});
		$routeProvider.when('/editCollection/:id', { templateUrl: '/de.fraunhofer.abm/template/editCollection.htm'});
		$routeProvider.when('/login', { templateUrl: '/de.fraunhofer.abm/template/login.htm'});
		$routeProvider.when('/register', { templateUrl: '/de.fraunhofer.abm/template/register.htm'});
		$routeProvider.when('/registered', { templateUrl: '/de.fraunhofer.abm/template/registered.htm'});
		$routeProvider.when('/createCollection', { templateUrl: '/de.fraunhofer.abm/template/createCollection.htm'});
		$routeProvider.when('/addToCollection', { templateUrl: '/de.fraunhofer.abm/template/addToCollection.htm'});
		$routeProvider.when('/applyCriteria', { templateUrl: '/de.fraunhofer.abm/template/applyCriteria.htm'});
		//$routeProvider.when('/my', { templateUrl: '/de.fraunhofer.abm/template/my.htm'});
		$routeProvider.when('/filters', { templateUrl: '/de.fraunhofer.abm/template/filters.htm'});
		$routeProvider.when('/filterResult/:id', { templateUrl: '/de.fraunhofer.abm/template/filterResult.htm'});
		$routeProvider.when('/view/:id', { templateUrl: '/de.fraunhofer.abm/template/view.htm'});
		$routeProvider.otherwise('/');
		
		GoogleSigninProvider.init({
	        client_id: '${googletoken}',
	    });
	}]);
	
	MODULE.run( function($rootScope, $location, $cookies) {
		$rootScope.loading = false;	
		$rootScope.page = function() {
			return $location.path();
		}

		$rootScope.user = $cookies.get('user');
	});

	var mainProvider = function() {};
})();

