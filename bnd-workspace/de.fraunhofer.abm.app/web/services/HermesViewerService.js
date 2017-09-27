/*angular.module('de.fraunhofer.abm').factory("hermesViewerService",  ['$uibModal', '$http', '$rootScope', 'Notification', function($uibModal, $http, $rootScope, Notification) {

   var self = this;
   self.isOpen = false;
   self.openingSocket = false;
   self.stepsToRegister = [];
   self.findingStep = false;
   self.steps = [];
   self.i;

   self.openSocket = function(){
        self.openingSocket = true;
        self.socket = new WebSocket("ws://localhost:8080/ws/hermes");
        self.socket.binaryType = "arraybuffer";
        self.socket.onopen = function(){
        console.log("Socket is open");
        self.isOpen = true;
        for(i=0;i<self.stepsToRegister.length;i++){
        self.socket.send(JSON.stringify(msg: 'listen', id: self.stepsToRegister[i]}));
        }
        self.stepsToRegister.splice(0,self.stepsToRegister.length);
        
        }
        self.socket.onmessage = function(e){
        resp = JSON.parse(e.data);
        if(resp.msg == "step_changed"){
        step = resp.step;
        if(step.name == "Delete Docker Container" && step.status  == "SUCCESS"){
              self.updateHermes(step.id);
              }
        
           }
        
       }
      self.socket.onclose = function(e){
      console.log("Connection closed");
      self.isOpen = false;
      }
      
   }
   
   self.updateHermes = function(stepId){
    self.findingStep = true;
    for(i=0;i<self.steps.length;i++){
          self.checkHermes(stepId,i)
          if(!self.findingStep){break;}

    }
  
   }

   self.checkHermes = function(stepId,i){
   $http({
			method: 'GET',
			url: '/rest/instance/' + self.steps[i].id
		}).then(
             function success(d){
             hermesResult = d.data;
             for(j=0;j<hermesResult.hermesSteps.length;j++){
             if(!self.findingStep){return;}
             hermesStep = hermesResult.hermesSteps[j];
             for(k=0;k<projectBuild.buildSteps.length;k++){
							if(projectBuild.buildSteps[k].id == stepId){
								self.findingStep = false;
								self.builds[i].progress = (j + 1) / buildResult.projectBuilds.length;
								if(buildResult.status == 'RUNNING' && self.builds[i].progress == 1){
									failed = self.checkFailure(buildResult);
									if(failed){
										self.builds[i].buildStatus = 'FAILED';
										Notification.success(self.builds[i].name + " failed to build");
									} else {
										self.builds[i].buildStatus = 'FINISHED';
										Notification.success(self.builds[i].name + " has been built");
									}
								} else {
									self.builds[i].buildStatus = buildResult.status;
								}
								return;
							}
						}
					}
				}, function failure(d){
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				})
   
   }

   

	return{
		launch: function() {
			var modalInstance = $uibModal.open({
				animation : true,
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'template/modalBuildViewer.htm',
				controller : 'modalBuildController',
				controllerAs : '$ctrl',
			});
		},
		version: undefined,
		collection: undefined
	};
}]);*/