angular.module('de.fraunhofer.abm').filter('fileSize', function(){
	return function(input){
		if(input < 1024){
			return input + ' kB';
		} else if(input < 1048576){
			return (input / 1024).toFixed(2) + ' MB';
		} else {
			return (input / 1048576).toFixed(2) + ' GB';
		}
	};
})