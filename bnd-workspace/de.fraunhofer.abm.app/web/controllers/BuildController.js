angular.module('de.fraunhofer.abm').controller("buildController", 
['$rootScope', '$scope', '$http', '$location', '$routeParams', 'buildService', 'modalLoginService', 'Notification',
function buildController($rootScope, $scope, $http, $location, $routeParams, buildService, modalLoginService, Notification) {
	var self = this;

	self.socket = null;
	self.isopen = false;
	self.versionId = $routeParams.id;
	self.buildResult = {
		id: "",
		status: "WAITING",
		projectBuilds: []
	};
	
	self.openSocket = function(buildId) {
		self.socket = new WebSocket("ws://localhost:8080/ws/build");
		self.socket.binaryType = "arraybuffer";
		self.socket.onopen = function() {
			console.log("Connected!");
			self.isopen = true;
			self.registerBuildListener(buildId);
		}
		self.socket.onmessage = function(e) {
			if (typeof e.data == "string") {
				var resp = JSON.parse(e.data);
				if(resp.msg == "update") {
					if(resp.data == 'build_process_finished') {
						self.buildResult.status = 'FINISHED';
						$scope.$apply();
					}
				} else if(resp.msg == "buildsteps") {
					var repoId = resp.repository;
					var steps = resp.steps;
					for(var i=0; i<self.buildResult.projectBuilds.length; i++) {
						var currentBuild = self.buildResult.projectBuilds[i];
						if(currentBuild.repositoryId == repoId) {
							currentBuild.buildSteps.clear();
							for(var i=0; i<steps.length; i++) {
								var step = steps[i];
								step.cssClass = 'panel-info';
								currentBuild.buildSteps.push(step);
							}
							break;
						}
					}
					$scope.$apply();
				} else if(resp.msg == "build_finished") {
					var repoId = resp.repository;
					var steps = resp.steps;
					for(var i=0; i<self.buildResult.projectBuilds.length; i++) {
						var currentBuild = self.buildResult.projectBuilds[i];
						if(currentBuild.repositoryId == repoId) {
							currentBuild.cssClass = 'panel-success'; // TODO do only, if all steps finished successfully
							break;
						}
					}
					$scope.$apply();
				} else if(resp.msg == "step_changed") {
					var step = resp.step;
					for(var i=0; i<self.buildResult.projectBuilds.length; i++) {
						var currentProjectBuild = self.buildResult.projectBuilds[i];
						for(var j=0; j<currentProjectBuild.buildSteps.length; j++) {
							var currentStep = currentProjectBuild.buildSteps[j];
							if(currentStep.id == step.id) {
								currentStep.status = step.status;
								currentStep.stderr = step.stderr;
								currentStep.stdout = step.stdout;
								if(step.status == 'IN_PROGRESS') {
									currentStep.cssClass = 'panel-warning';
									currentProjectBuild.cssClass = 'panel-warning';
								} else if(step.status == 'SUCCESS') {
									currentStep.cssClass = 'panel-success';
								} else if(step.status == 'FAILED') {
									currentStep.cssClass = 'panel-danger';
									currentProjectBuild.cssClass = 'panel-danger';
								}
								break;
							}
						}
					}
					$scope.$apply();
				} else {
					console.log("Text message received: "+ e.data);
				}
			} else {
				var arr = new Uint8Array(e.data);
				var hex = '';
				for (var i = 0; i < arr.length; i++) {
					hex += ('00' + arr[i].toString(16)).substr(-2);
				}
				console.log("Binary message received: "+ hex);
			}
		}
		self.socket.onclose = function(e) {
			console.log("Connection closed.");
			self.socket = null;
			self.isopen = false;
		}
	};
	
	self.cancelBuild = function(buildId) {
		if (self.isopen) {
			console.log('Cancel build', buildId);
			self.socket.send(JSON.stringify({msg: 'cancel', id: buildId}));
		} else {
			console.log("Connection not opened.")
		}
	};

	
	self.registerBuildListener = function(buildId) {
		if (self.isopen) {
			self.socket.send(JSON.stringify({msg: 'listen', id: buildId}));
			console.log("Registering listener for build " + buildId);
		} else {
			console.log("Connection not opened.")
		}
	};
	
	self.downloadArchive = function(buildResultId) {
		location.href = '/download/' + buildResultId;
	}

	self.deleteBuild = function(buildResultId) {
		$rootScope.loading = true;
		$http({
			method: 'DELETE',
			url: '/rest/build/' + buildResultId
		}).then(
				function success(d) {
					$location.path('/editCollection/' + d.data);
				}, function failure(d) {
					if(d.status == 401) {
						modalLoginService();
					} else {
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				})['finally'](function() {
					$rootScope.loading = false;
				});
	}
	
	self.loadBuildResult = function(versionId) {
		$rootScope.loading = true;
		$http({
			method: 'GET',
			url: '/rest/build/' + versionId
		}).then(
				function success(d) {
					self.buildResult = d.data;
					if(self.buildResult.status == 'RUNNING' && self.socket == null) {
						console.log('Opening websocket');
						self.openSocket(self.buildResult.id);
					}
					
					for(var i=0; i<self.buildResult.projectBuilds.length; i++) {
						var currentBuild = self.buildResult.projectBuilds[i];
						currentBuild.cssClass = "panel-info";
						for(var j=0; j<currentBuild.buildSteps.length; j++) {
							var currentStep = currentBuild.buildSteps[j];
							if(currentStep.status == 'IN_PROGRESS') {
								currentStep.cssClass = 'panel-warning';
								currentBuild.cssClass = 'panel-warning';
							} else if(currentStep.status == 'SUCCESS') {
								currentStep.cssClass = 'panel-success';
							} else if(currentStep.status == 'WAITING') {
								currentStep.cssClass = 'panel-info';
							} else if(currentStep.status == 'FAILED') {
								currentStep.cssClass = 'panel-danger';
								currentBuild.cssClass = 'panel-danger';
							}
						}
						
						var allGood = currentBuild.buildSteps.length > 0;
						for(var j=0; j<currentBuild.buildSteps.length; j++) {
							var currentStep = currentBuild.buildSteps[j];
							if(currentStep.status != 'SUCCESS') {
								allGood = false;
							}
						}
						if(allGood) {
							currentBuild.cssClass = 'panel-success';
						}
					}
					
					console.log(self.buildResult);
				}, function failure(d) {
					if(d.status == 401) {
						modalLoginService();
					} else {
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				})['finally'](function() {
					$rootScope.loading = false;
				});
	};
	
	self.loadBuildResult(self.versionId);	
}]);