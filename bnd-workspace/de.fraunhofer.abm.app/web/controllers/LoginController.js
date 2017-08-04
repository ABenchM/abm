angular.module('de.fraunhofer.abm').controller('loginController', 
[ '$rootScope', '$scope', '$http', '$location', '$cookies', 'ngCart', 'GoogleSignin', 'Notification', 'buildViewerService',
function($rootScope, $scope, $http, $location, $cookies, ngCart, GoogleSignin, Notification, buildViewerService) {
	$scope.credentials = {};
	$scope.login = function() {
		$rootScope.loading = true;
		$http.post('/rest/login', $scope.credentials, null).then(
				function() {
					$location.path('/');
					d = new Date();
					d.setTime(d.getTime() + 43200000)
					$rootScope.user = $scope.credentials.username;
					$cookies.put('user', $scope.credentials.username, {expires: d});
				}, function(d) {
					if(d.status == 401) {
						Notification.error('Wrong username or password');
						$location.path('/login');
					} else {
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				})['finally'](function() {
					$rootScope.loading = false;
				});
	};
	
	$scope.logout = function() {
		$http.get('/rest/logout').then(
			function(d) {
				$location.path('/login');
				$rootScope.user = undefined;
				$rootScope.userCollections = undefined;
				$cookies.remove('user');
				ngCart.empty();
			}, function(d) {
				$location.path('/');
			});
	};
	
	$scope.googleLogin = function() {
		GoogleSignin.signIn().then(
			function (user) {
				$rootScope.loading = true;
				var username = user.El;
				$http.post('/rest/login', {username: 'google-oauth', password: user.Zi.id_token}, null).then(
					function() {
						$location.path('/');
						$rootScope.user = username;
						$cookies.put('user', username);
					}, function(d) {
						if(d.status == 401) {
							Notification.error('Wrong username or password');
							$location.path('/login');
						} else {
							Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
						}
					})['finally'](function() {
						$rootScope.loading = false;
					});
	        }, function (err) {
	        	Notification.error('Failed with ['+ err.status + '] '+ err.statusText);
	        });
	}
}]);