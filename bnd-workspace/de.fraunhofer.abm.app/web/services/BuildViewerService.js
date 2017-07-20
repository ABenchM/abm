angular.module('de.fraunhofer.abm').factory("buildViewerService", ['$uibModal', '$http', function($uibModal, $http) {
	
	var self = this;
	self.isOpen = false;
	self.openingSocket = false;
	self.buildsToRegister = [];
	self.findingStep = false;
	self.builds = [];
	self.i;
	
	self.openSocket = function(){
		self.openingSocket = true;
		self.socket = new WebSocket("ws://localhost:8080/ws/build");
		self.socket.binaryType = "arraybuffer";
		self.socket.onopen = function() {
			console.log("Socket is open!");
			self.isOpen = true;
			for(i=0;i<self.buildsToRegister.length;i++){
				self.socket.send(JSON.stringify({msg: 'listen', id: self.buildsToRegister[i]}));
			}
			self.buildsToRegister.splice(0, self.buildsToRegister.length);
		}
		self.socket.onmessage = function(e) {
			resp = JSON.parse(e.data);
			if(resp.msg == "step_changed"){
				step = resp.step;
				if(step.name == "Delete Docker image" && step.status == "SUCCESS"){
					self.updateBuild(step.id);
				}
			}
		}
		self.socket.onclose = function(e) {
			console.log("Connection closed");
			self.isOpen = false;
		}
	}
	
	self.updateBuild = function(stepId){
		self.findingStep = true;
		for(i=0;i<self.builds.length;i++){
			self.checkBuild(stepId, i)
			if(!self.findingStep){break;}
		}
	}
	
	self.checkBuild = function(stepId, i){
		$http({
			method: 'GET',
			url: '/rest/build/' + self.builds[i].id
		}).then(
				function success(d) {
					buildResult = d.data;
					for(j=0;j<buildResult.projectBuilds.length;j++){
						if(!self.findingStep){return;}
						projectBuild = buildResult.projectBuilds[j];
						for(k=0;k<projectBuild.buildSteps.length;k++){
							if(projectBuild.buildSteps[k].id == stepId){
								self.findingStep = false;
								self.builds[i].progress = (j + 1) / buildResult.projectBuilds.length;
								if(buildResult.status == 'RUNNING' && self.builds[i].progress == 1){
									self.builds[i].buildStatus = 'FINISHED';
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
	
	return {
		launch: function() {
			var modalInstance = $uibModal.open({
				animation : true,
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'template/modalBuildViewer.htm',
				controller : 'modalBuildController',
				controllerAs : '$ctrl',
				windowTopClass: 'side',
				size: 'lg',
			});
		},
		
		builds: self.builds,
		initialSelection: undefined,
		
		addListener: function(buildId){
			if(!self.isOpen){
				self.buildsToRegister.push(buildId);
				if(!self.openingSocket){self.openSocket();}
			} else {
				self.socket.send(JSON.stringify({msg: 'listen', id: buildId}));
			}
		}
	};
}]);