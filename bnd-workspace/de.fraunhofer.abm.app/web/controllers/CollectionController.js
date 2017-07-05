angular.module('de.fraunhofer.abm').controller("collectionController", 
['$rootScope', '$scope', '$http', '$location', '$route', 'ngCart', 'modalLoginService', 
	'collectionService', 'commitSelectorService', 'buildService', 'buildViewerService','Notification',
function collectionController($rootScope, $scope, $http, $location, $route, ngCart, modalLoginService, 
		collectionService, commitSelectorService, buildService, buildViewerService, Notification) {
	var self = this;
	self.collection = collectionService.collection;
	self.version = collectionService.version;
	self.showCollection = false;
	self.disableBuild = false;
	
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
	
	$scope.loadUserCollections = function() {
		$rootScope.loading = true;
		$http({
		    method: 'GET',
			url: '/rest/collection',
			params: {'user': $rootScope.user}
		}).then(
			function(resp) {
				$scope.gridOpts.data = resp.data;
			}, function(d) {
				if(d.status == '403') {
					$location.path('/login');
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					$location.path('/');
				}
			})['finally'](function() {
				$rootScope.loading = false;
			});
	}
	
	self.edit = function(collection) {
		collectionService.setCollection(collection);
		self.collection = collectionService.getCollection();
		self.version = collectionService.version;
		self.showCollection = true;
	}

	self.save = function() {
		$rootScope.loading = true;
		
		self.collection.creation_date = new Date();
		self.collection.privateStatus = false;

		var version = {
			number: 1,
		    creationDate : new Date(),
		    comment : 'Initial Version'
		};
		self.collection.versions = [];
		self.collection.versions.push(version);
		
		version.commits = [];
		var items = ngCart.getItems();
		for(var i=0; i<items.length; i++) {
			var commit = {
				commitId: 'HEAD'
			};
			commit.repository = items[i]._data;
			commit.branchId = commit.repository.defaultBranch;
			version.commits.push(commit);
		}
		
		$http.post('/rest/collection', self.collection, null).then(
				function() {
					$location.path('/my');
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
	
	self.update = function () {
		self.saving = true;
		$http.put('/rest/collection', self.collection, null).then(
			function() {
				$location.path('/my');
			}, function(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
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
				var d = $scope.gridOpts.data;
				for(var i=0; i<d.length; i++) {
					if(d[i].id == id) {
						d.splice(i,1);
					}
				}
			}, function(d) {
				if(d.status == 403) {
					modalLoginService();
				} else {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
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
						Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
					}
				}
		)['finally'](function() {
			self.saving=false;
		});
	}

	self.addCart = function(version) {
		self.saving=true;

		var updatedVersion = JSON.parse(JSON.stringify(version));
		var items = ngCart.getItems();
		for(var i=0; i<items.length; i++) {
			var commit = {
				commitId: 'HEAD'
			};
			commit.repository = items[i]._data;
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
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				}
			}
		)['finally'](function() {
			self.saving=false;
		});
	}
	
	self.build = function() {
		self.deleteBuild(self.version);
		$http.post('/rest/build', self.version, null).then(
				function(d) {
					var buildId = d.data;
					//collectionService.version.frozen = true;
					self.version.frozen = true;
					buildViewerService.builds.push({"id": self.version.id, "name": self.collection.name, "versionNum": self.version.number});
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
			buildViewerService.builds.push({"id": version.id, "name": self.collection.name, "versionNum": version.number});
			targetTab = buildViewerService.builds.length - 1;
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
	
	self.findTab = function(item){
		return (item.id == this.id);
	}
}]);