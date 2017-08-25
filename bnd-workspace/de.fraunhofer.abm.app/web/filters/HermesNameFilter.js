angular.module('de.fraunhofer.abm').filter('hermesName', function(){
	return function(input){
		return input.replace("org.opalj.hermes.queries.", "");
	};
})