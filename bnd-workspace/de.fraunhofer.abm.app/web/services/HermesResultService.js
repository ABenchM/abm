angular.module('de.fraunhofer.abm').factory("hermesResultService", ['$http', function($http) {
	return{
		getHermesStatus: function(versionId) {
		
			return $http({
				method: 'GET',
				url: '/rest/instance/' + versionId			
			}).then( function(response)
					{
				      console.log(response.data.status);
				      return response.data.status;
					});
		
		}
	};
}]);