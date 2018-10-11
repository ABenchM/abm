angular.module('de.fraunhofer.abm').controller("collectionController", 
['$rootScope', '$scope', '$http', '$location', '$route', '$routeParams', 'ngCart', 'modalLoginService', 
	'collectionService', 'commitSelectorService', 'buildViewerService','Notification', 'hermesViewerService','modalHermesService', 
function collectionController($rootScope, $scope, $http, $location, $route, $routeParams, ngCart, modalLoginService, 
		collectionService, commitSelectorService, buildViewerService, Notification,hermesViewerService,modalHermesService ) {
	var self = this;
	self.collection = collectionService.collection;
	self.version = collectionService.version;
	self.showCollection = false;
	self.disableBuild = false;
	self.disableHermes = false;
	self.repositoryList = collectionService.toCreate;
	$rootScope.running = false;
	self.running = $rootScope.running;
	
	$scope.request = {};
	self.showDeleteAccount = false;
	self.showGetUsers = false;
	self.showApproveRejectUser = true;
	
	self.approveUser = function(user){
 		alert("Approve User : " + user);
 		console.log("Approve user : " + user);
		$rootScope.loading = true;
		$scope.request.username = user;
		$scope.request.isApprove = true;
		$http.post('/rest/approval', $scope.request, null).then(
			function(d){
				if(d.data){
					console.log("data : " + d.data);
				}
			}, function(d){
				Notification.error('Internal error: Approve user cannot be done at the moment. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
			})['finally'](function() {
				$rootScope.loading = false
			});	
	};
	
	self.rejectUser = function(user){
		alert("Reject User : " + user);
 		console.log("Reject user : " + user);
		$rootScope.loading = true;
		$scope.request.username = user;
		$scope.request.isApprove = false;
		$http.post('/rest/approval', $scope.request, null).then(
			function(d){
				if(d.data){
					console.log("data : " + d.data);
				}
			}, function(d){
				Notification.error('Internal error: Reject user cannot be done at the moment. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
			})['finally'](function() {
				$rootScope.loading = false
			});	
	};
	self.loadAllUserInfo = function(){
		$rootScope.loading = true;
		$http({
		    method: 'GET',
			url: '/rest/userList',
			params: {'approved': 1}
		}).then(
			function(resp) {
				if(resp.data[0] != undefined){
					self.edit(resp.data[0]);
				} else {
					Notification.error('Internal error: the collections cannot be retrieved. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
					$location.path('/');
				}
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				$location.path('/');
			})['finally'](function() {
				$rootScope.loading = false;
			});
	};
	
 	self.deleteAccount = function(user){
 		alert(user);
 		console.log("delete user : " + user);
		$rootScope.loading = true;
		$scope.request.username = user;
		$http.post('/rest/deleteUser', $scope.request, null).then(
			function(d){
				if(d.data){
					console.log("data : " + d.data);
				}
			}, function(d){
				Notification.error('Internal error: Delete user cannot be done at the moment. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
			})['finally'](function() {
				$rootScope.loading = false
			});	
	};
	
	self.adminDeleteAccounts = function(){
 		alert("Admin delete");
		$rootScope.loading = true;
		var deleteUserList = [];
		deleteUserList.push("delete1");
		deleteUserList.push("delete2");
		$scope.request.deleteUsers =  deleteUserList.toString();//"{ deleteUsers : "+ deleteUserList + "}";
		$http.post('/rest/adminDeleteUsers', $scope.request, null).then(
			function(d){
				if(d.data){
					console.log("data : " + d.data);
				}
			}, function(d){
				Notification.error('Internal error: Delete users cannot be done at the moment. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
			})['finally'](function() {
				$rootScope.loading = false
			});	
	};
	
	var columnDefinition = [
		{ name: 'name' },
		{ name: 'description' },
		{ displayName: 'Creation date', cellTemplate: '<div><p>{{row.versions[0].creationDate}}</p></div>'},
		{ displayName: 'Actions', name: 'actions', 
			cellTemplate: '<div>'+
			'<button ng-click="grid.appScope.cc.edit(row.entity)" style="margin: 3px" class="btn btn-xs"><i class="glyphicon glyphicon-pencil"></i></button>'+
			'<button ng-click="grid.appScope.cc.remove(row.entity.id)" class="btn btn-xs btn-danger" title="Delete" confirm="Removal is irreversible! Continue?"><i class="glyphicon glyphicon-trash"></i></button>'+
			'</div>'
		}
		
    ];
	var data = [];
	$scope.gridOpts = {
	    columnDefs: columnDefinition,
	    data: data
	};
	
	
	
	self.initilize = function(){
		if($rootScope.user == undefined){return;}
		else if($routeParams.id != undefined){self.loadCollection($routeParams.id);}
		else{$scope.loadUserCollections();}
	}
	
	self.loadCollection = function(collectionId){
		$rootScope.loading = true;
		$http({
		    method: 'GET',
			url: '/rest/collection',
			params: {'id': collectionId}
		}).then(
			function(resp) {
				if(resp.data[0] != undefined){
					self.edit(resp.data[0]);
				} else {
					Notification.error('Internal error: the collections cannot be retrieved. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
					$location.path('/');
				}
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				$location.path('/');
			})['finally'](function() {
				$rootScope.loading = false;
			});
	};
	
	$scope.loadUserCollections = function() {
		$rootScope.loading = true;
		$http({
		    method: 'GET',
			url: '/rest/collection',
			params: {'user': $rootScope.user}
		}).then(
			function(resp) {
				$rootScope.userCollections = resp.data;
			}, function(d) {
				if(d.status == '403') {
					$location.path('/login');
				} else {
					Notification.error('Internal error: the collections cannot be retrieved. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
					$location.path('/');
				}
			})['finally'](function() {
				if(self.collection == undefined){
					if($rootScope.userCollections[0] != undefined){
						self.edit($rootScope.userCollections[0]);
					}
				}
				$rootScope.loading = false;
			});
	}
	
	
             
	
	self.edit = function(collection) {
		collectionService.setCollection(collection);
		self.collection = collectionService.getCollection();
		self.version = collectionService.version;
		self.showCollection = true;
	}
	
	self.open = function(collection){
		if(collection.privateStatus){
			self.edit(collection);
			$location.path('/editCollection/' + collection.id);
		} else {		
			$location.path('/view/' + collection.id);
		}
	}
	
	self.save = function(repositoryList) {
		if($rootScope.user == undefined){
			Notification.error('Please login before creating a collection');
			return;
		}
		$rootScope.loading = true;
		
		self.collection.creation_date = new Date();
		self.collection.privateStatus = true;

		var version = {
			number: 1,
		    creationDate : new Date(),
		    comment : 'Initial Version'
		};
		self.collection.versions = [];
		self.collection.versions.push(version);
		
		if(repositoryList.length == 0){
			for(var i=0; i < ngCart.getTotalItems(); i++){
				repositoryList.push(ngCart.getCart().items[i].getData());
			}
		}
		
		version.commits = [];
		for(var i=0; i<repositoryList.length; i++) {
			var commit = {
				commitId: 'HEAD'
			};
			commit.repository = repositoryList[i];
			commit.branchId = commit.repository.defaultBranch;
			version.commits.push(commit);
		}
		
		$http.post('/rest/collection', self.collection, null).then(
				function() {
					$location.path('/editCollection');
				}, function(d) {
					if(d.status == 403) {
						modalLoginService();
					} else {
						Notification.error('Internal error: the collections cannot be saved. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
					}
				})['finally'](function() {
					collectionService.toCreate = [];
					$rootScope.loading = false;
				});
	}
	
	self.update = function () {
		self.saving = true;
		$http.put('/rest/collection', self.collection, null).then(
			function() {
				$location.path('/editCollection');
			}, function(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Internal error: the collections cannot be updated. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
				}
			})
			['finally'](function() {
				self.saving = false;
			});
	}
	
	self.remove = function(id) {
		$http({
			method: 'DELETE',
			url: '/rest/collection/' + id
		}).then(
			function() {
				var d = $rootScope.userCollections;
				for(var i=0; i<d.length; i++) {
					if(d[i].id == id) {
						d.splice(i,1);
					}
				}
			}, function(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Internal error: the collections cannot be deleted. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
				}
			}
		)['finally'](function() {
		});
	}
	

	// toggle selection for a given commit id
	self.selection = [];
	self.toggleSelection = function toggleSelection(id) {
	    var idx = self.selection.indexOf(id);
	    if (idx > -1) {
	        self.selection.splice(idx, 1);
	    } else {
	        self.selection.push(id);
	    }
	};
	
	self.removeSelectedCommits = function() {
		if(self.selection.length == 0) {
			return;
		}
		
		self.saving=true;
		$http({
			method: 'POST',
			url: '/rest/commit',
			data: {
				action: 'delete_multi',
				ids: self.selection
			}
		}).then(
			function() {
				var d = self.version.commits;
				for(var j=0; j<self.selection.length; j++) {
					for(var i=0; i<d.length; i++) {
						if(d[i].id == self.selection[j]) {
							d.splice(i,1);
							break;
						}
					}
				}
				self.selection.clear();
			}, function(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				}
			}
		)['finally'](function() {
			self.saving=false;
		});
	}

	self.removeVersion = function(id) {
		self.saving=true;
		$http({
			method: 'DELETE',
			url: '/rest/version/' + id
		}).then(
				function() {
					var d = self.collection.versions;
					for(var i=0; i<d.length; i++) {
						if(d[i].id == id) {
							d.splice(i,1);
							self.version = self.collection.versions[0];
							collectionService.version = self.version;
							$route.reload();
							break;
						}
					}
				}, function(d) {
					if(d.status == 403) {
						modalLoginService();
					} else {
						Notification.error('Internal error: the version cannot be deleted. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
					}
				}
		)['finally'](function() {
			self.saving=false;
		});
	}

	self.addProjects = function(version) {
		$rootScope.loading = true;

		var updatedVersion = JSON.parse(JSON.stringify(version));
		var projects = collectionService.toAdd;
		for(var i=0; i<projects.length; i++) {
			var commit = {
				commitId: 'HEAD'
			};
			commit.repository = projects[i];
			commit.branchId = commit.repository.defaultBranch;
			updatedVersion.commits.push(commit);
		}
		
		$http({
			method: 'PUT',
			url: '/rest/version/',
			data: updatedVersion
		}).then(
			function(d) {
				self.version = d.data; 
				collectionService.version = self.version;
				var col = collectionService.collection;
				for(var i=0; i<col.versions.length; i++) {
					if(col.versions[i].id == self.version.id) {
						col.versions.splice(i, 1, self.version);
					}
				}
			}, function(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Internal error: the projects cannot be added. Please try again later. If the error persists, please report it here: https://github.com/ABenchM/abm/issues');
				}
			}
		)['finally'](function() {
			$location.path('/editCollection');
			$rootScope.loading = false;
		});
	}
	
	self.build = function() {
		$http.post('/rest/build', self.version, null).then(
				function(d) {
					var buildId = d.data;
					//collectionService.version.frozen = true;
					self.version.frozen = true;
					buildViewerService.builds.push({"id": self.version.id, "name": self.collection.name, "versionNum": self.version.number, "progress": 0, "buildStatus": 'RUNNING', "hidden": false});
					targetTab = buildViewerService.builds.length - 1;
					self.getBuildProgress(self.version.id, targetTab);
				}, function(d) {
					if(d.status == 403) {
						modalLoginService();
					} else {
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				})['finally'](function() {
					$rootScope.loading = false;
				});
	}
	
	self.selectCommit = function(commit) {
		collectionService.commit = commit;
		commitSelectorService();
	}
	
	self.deriveVersion = function() {
		self.disabled=true;
		$http({
		    method: 'POST',
			url: '/rest/version/derive',
			data: collectionService.version
		}).then(
			function(resp) {
				var derivedVersion = resp.data;
				collectionService.collection.versions.push(derivedVersion);
				collectionService.collection.version = derivedVersion;
				self.version = derivedVersion;
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})['finally'](function() {
				self.disabled=false;
			});
	}
	
	self.addBuild = function(version) {
		targetTab = buildViewerService.builds.findIndex(self.findTab, version);
		if(targetTab < 0){
			buildViewerService.builds.push({"id": version.id, "name": self.collection.name, "versionNum": version.number, "progress": 0, "buildStatus": '', "hidden": false});
			targetTab = buildViewerService.builds.length - 1;
			self.getBuildProgress(version.id, targetTab);
		} else {
			buildViewerService.builds[targetTab].hidden = false;
		}
		buildViewerService.initialSelection = buildViewerService.builds[targetTab];
		buildViewerService.launch();
		$rootScope.hideSidebar = true;
	}
	
	
	
	
	self.unfreeze = function(version) {
		self.disableBuild=true;
		$http({
			method: 'GET',
			url: '/rest/build/' + version.id
		}).then(
			function success(d) {
				self.buildResult = d.data;
				if(self.buildResult.status == 'RUNNING' || self.buildResult.status == 'WAITING') {
					repoId = undefined;
					self.socket = new WebSocket("ws://localhost:8080/ws/build");
					self.socket.binaryType = "arraybuffer";
					self.socket.onopen = function() {
						console.log("Socket is open!");
						self.socket.send(JSON.stringify({msg: 'listen', id: self.buildResult.id}));
						console.log('Sending command');
						self.socket.send(JSON.stringify({msg: 'cancel', id: self.buildResult.id}));
						if(self.buildResult.status == 'WAITING'){
							self.socket.close();
						}
					}
					self.socket.onmessage = function(e) {
						resp = JSON.parse(e.data)
						if(resp.msg == "build_finished" && resp.repository == repoId){
							self.socket.close();
						} else if(resp.msg == "build_cancelled"){
							repoId = resp.repository;
						}
					}
					self.socket.onclose = function(e) {
						console.log("Connection closed, deleting build");
						self.deleteBuild(version);
					}
				} else {
					self.deleteBuild(version);
				}
			}, function failure(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				}
			})['finally'](function() {
				targetTab = buildViewerService.builds.findIndex(self.findTab, self.version);
				if(targetTab >= 0){
					buildViewerService.builds.splice(targetTab, 1);
				}
				self.disableBuild=false;
			});
	}
	
	self.deleteBuild = function(version){
		$http({
		    method: 'POST',
			url: '/rest/version/unfreeze',
			data: version
		}).then(
			function(resp) {
				self.version = version;
				self.collection.version = version;
				self.collection.version.frozen = false;
			}, function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			})
	}
	
	self.makePublic = function(collection){
		collection.privateStatus = false;
		self.update();
	}
	
	self.findTab = function(item){
		return (item.id == this.id);
	}
	
	self.nameSort = function(){
		$rootScope.userCollections.sort(function(a, b){return a.name > b.name});
	}
	
	self.descSort = function(){
		$rootScope.userCollections.sort(function(a, b){return a.description > b.description});
	}
	
	self.dateSort = function(){
		$rootScope.userCollections.sort(function(a, b){return a.creation_date  > b.creation_date });
	}
	
	self.getBuildProgress = function(versionId, targetTab){
		$http({
			method: 'GET',
			url: '/rest/build/' + versionId
		}).then(
			function success(d) {
				build = d.data;
				progress = 0;
				if(build.status == "RUNNING"){
					for(i=0;i<build.projectBuilds.length;i++){
						buildProject = build.projectBuilds[i];
						for(j=0;j<buildProject.buildSteps.length;j++){
							if(buildProject.buildSteps[j].status == "IN_PROGRESS"){
								progress = i/build.projectBuilds.length;
							}
						}
					}
				} else if(build.status == "FINISHED"){
					progress = 1;
				}
			}, function failure(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				}
				progress =  0;
			})['finally']( function (){
				buildViewerService.builds[targetTab].progress = progress;
				buildViewerService.builds[targetTab].buildStatus = build.status;
				buildViewerService.addListener(build.id);
			});
	}
	
	self.runFilter = function(version){
	     $http({
			method: 'GET',
			url: '/rest/build/'+version.id
		}).then(
				function success(d) {
					self.buildResult = d.data;
					if(self.buildResult.status == 'RUNNING'){
						Notification.error('Build is in progress, try again later');
					} else {
						
						self.version.filtered = true;
						self.running = true;
		                modalHermesService.version = version;
		                modalHermesService.collection = self.collection;
		                modalHermesService.launch();
					}
        	});
        }
	

   self.showHermesResults = function(version){
		
		self.version.filtered = true;
		self.running = true;
		hermesViewerService.version = version;
		hermesViewerService.collection = self.collection;
		hermesViewerService.launch();
		
	}
	
	self.cancelFilter = function(){
	
	}
	
	self.showFilter = function(version){
				
		buildViewerService.launch();
		
		
	}
	
	

	
	
	
	self.removeFilter = function(version){
	    self.disableHermes = true;
	    self.version.filtered = false;
		$rootScope.loading = true;
		$http({
			method: 'GET',
			url: '/rest/instance/' + version.id			
		}).then(
			function success(d) {
			    console.log(d);
			    if( d.data.status == 'RUNNING'){
					Notification.error('Hermes is in progress, try again later');
				} else {
				$http.delete('/rest/instance/' + d.data.id).then(
					function success(d){
						
					}, function failure(d){
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}) }
			}, function failure(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				}
				progress =  0;
			})['finally']( function (){
				$rootScope.loading = false;
			});
	}
	
	self.back = function(){
		$location.path('/');
	}
	
	self.initilize();
}]);
