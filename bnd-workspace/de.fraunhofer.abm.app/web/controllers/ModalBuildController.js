angular.module('de.fraunhofer.abm').controller('modalBuildController', function($uibModalInstance, $rootScope, $scope, $http, buildViewerService, modalLoginService, Notification) {
	var $ctrl = this;

	$ctrl.tabs = buildViewerService.builds;
	$ctrl.showing = buildViewerService.initialSelection;

	$ctrl.select = function(target){
		if($ctrl.socket != null){$ctrl.socket.close();}
		$ctrl.showing = target;
		$ctrl.loadBuild(target.id);
	}

	$ctrl.openSocket = function(buildId) {
		$ctrl.socket = new WebSocket("ws://localhost:8080/ws/build");
		$ctrl.socket.binaryType = "arraybuffer";
		$ctrl.socket.onopen = function() {
			console.log("Connected!");
			$ctrl.isopen = true;
			$ctrl.socket.send(JSON.stringify({msg: 'listen', id: buildId}));
		}
		$ctrl.socket.onmessage = function(e) {
			if (typeof e.data == "string") {
				var resp = JSON.parse(e.data);
				if (resp.msg == "build_cancelled"){

					if($ctrl.socket != null){$ctrl.socket.close();}
					$uibModalInstance.close();
				}
				else if(resp.msg == "update") {
					if(resp.data == 'build_process_finished') {
						$scope.build.status = 'FAILED';
						for(var i=0; i<$scope.build.projectBuilds.length; i++) {
							var currentBuild = $scope.build.projectBuilds[i];
							allGood = true;
							for(j=0;j<projectBuild.buildSteps.length;j++){
								if(projectBuild.buildSteps[j].status != "SUCCESS"){
									allGood = false;
								}
							}
							if(allGood){$scope.build.status = 'FINISHED';}
						}
						$scope.$apply();
					}
				} else if(resp.msg == "buildsteps") {
					if($scope.build.status == 'WAITING'){$scope.build.status = 'RUNNING';}
					var repoId = resp.repository;
					var steps = resp.steps;
					for(var i=0; i<$scope.build.projectBuilds.length; i++) {
						var currentBuild = $scope.build.projectBuilds[i];
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
					for(var i=0; i<$scope.build.projectBuilds.length; i++) {
						var currentBuild = $scope.build.projectBuilds[i];
						if(currentBuild.repositoryId == repoId) {
							currentBuild.cssClass = 'panel-success';
							for(var j=0; j<currentBuild.buildSteps.length; j++) {
								var currentStep = currentBuild.buildSteps[j];
									if(currentStep.status == 'FAILED') {
										currentBuild.cssClass = 'panel-danger';
										break;
									} else if(currentStep.status == 'CANCELLED') {
										currentBuild.cssClass = 'panel-info';
										$scope.build.status = 'CANCELLED';
										break;
									}
							}
							break;
						}
					}
					$scope.$apply();
				} else if(resp.msg == "step_changed") {
					var step = resp.step;
					for(var i=0; i<$scope.build.projectBuilds.length; i++) {
						var currentProjectBuild = $scope.build.projectBuilds[i];
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
		$ctrl.socket.onclose = function(e) {
			console.log("Connection closed.");
			$ctrl.socket = null;
			$ctrl.isopen = false;
		}
	};

	$ctrl.cancelBuild = function(buildId) {
		//$scope.build.status = 'CANCELLED';
		if ($ctrl.isopen) {
			console.log('Cancel build', buildId);
			$ctrl.socket.send(JSON.stringify({msg: 'cancel', id: buildId}));
		} else {
			console.log("Connection not opened.")
		}
	}

	$ctrl.deleteBuild = function(buildResultId) {
		$rootScope.loading = true;
		$http({
			method: 'DELETE',
			url: '/rest/build/' + buildResultId
		}).then(
				function success(d) {
					$ctrl.unfreeze($ctrl.showing.id);
					targetTab = buildViewerService.builds.findIndex($ctrl.findTab, $ctrl.showing.id);
					buildViewerService.builds.splice(targetTab, 1);
					$ctrl.close();
				}, function failure(d) {
					if(d.status == 403) {
						modalLoginService();
					} else {
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				})['finally'](function() {
					$rootScope.loading = false;
				});
	}

	$ctrl.unfreeze = function(versionId){
		for(i=0;i<$rootScope.userCollections.length;i++){
			collection = $rootScope.userCollections[i];
			for(j=0;j<collection.versions.length;j++){
				version = collection.versions[j];
				if(version.id == versionId){
					version.frozen = false;
				}
			}
		}
	}

	$ctrl.downloadArchive = function(buildResultId) {
		location.href = '/download/' + buildResultId;
	}

	$ctrl.loadBuild = function(versionId){
		$rootScope.loading = true;
		$http({
			method: 'GET',
			url: '/rest/build/' + versionId
		}).then(
				function success(d) {
					$scope.build = d.data;
					if(($scope.build.status == 'RUNNING' || $scope.build.status == 'WAITING') && $ctrl.socket == null) {
						console.log('Opening websocket');
						$ctrl.openSocket($scope.build.id);
					}

					for(var i=0; i<$scope.build.projectBuilds.length; i++) {
						var currentBuild = $scope.build.projectBuilds[i];
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
							} else if(currentStep.status == 'FAILED') {
								currentStep.cssClass = 'panel-warning';
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

					//console.log(self.buildResult);
				}, function failure(d) {
					if(d.status == 403) {
						modalLoginService();
					} else {
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				})['finally'](function() {
					$rootScope.loading = false;
				});
	}

	$ctrl.dismiss = function(tabId){
		targetTab = buildViewerService.builds.findIndex($ctrl.findTab, tabId);
		buildViewerService.builds[targetTab].hidden = true;
		if($ctrl.showing.id == tabId){
			$ctrl.close();
		}
	}

	$ctrl.close = function(){
		if($ctrl.socket != null){$ctrl.socket.close();}
		$uibModalInstance.close();
	}

	$ctrl.findTab = function(item){
		return item.id == this;
	}

	$scope.$on('modal.closing', function(event, reason, closed){
        setTimeout(function(){$rootScope.hideSidebar = false}, 200);
    });

	$ctrl.loadBuild(buildViewerService.initialSelection.id);
});
