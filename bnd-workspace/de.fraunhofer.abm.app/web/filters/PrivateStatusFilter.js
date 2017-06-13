angular.module('de.fraunhofer.abm').filter('privateFilter', function(){
	return function(input){
		if(input){
			return 'Private';
		} else {
			return 'Public';
		}
	};
})