angular.module('de.fraunhofer.abm').filter('tabTitle', function(){
	return function(input){
		return "Building " + input.name;
	};
})