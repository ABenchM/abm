angular.module('de.fraunhofer.abm').filter('statusColour', function(){
	return function(input){
		if(input == 'RUNNING'){return 'rgb(102, 217, 255)';}
		else if(input  == 'FINISHED'){return 'rgb(140, 255, 102)';}
		else if(input == 'CANCELLED'){return 'rgb(255, 232, 102)';}
		else{return 'gray';}
	};
})