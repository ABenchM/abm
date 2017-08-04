angular.module('de.fraunhofer.abm').filter('siteName', function(){
	return function(input){
		if(input.includes("github.com")){return 'Github';}
		else if(input.includes("bitbucket.com")){return 'Bitbucket';}
		else if(input.includes("maven.com")){return 'Maven';}
		else{return 'Unknown';}
	};
})