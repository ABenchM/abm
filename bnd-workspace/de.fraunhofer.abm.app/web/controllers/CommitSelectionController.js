angular.module('de.fraunhofer.abm').controller("commitSelectionController", 
['$rootScope', '$scope', '$http', '$uibModalInstance', 'collectionService',
function commitDropdownController($rootScope, $scope, $http, $uibModalInstance, collectionService) {
	var $ctrl = this;
	
	$ctrl.page = 1;
	$ctrl.totalItems = 1000;
	$ctrl.selected = 'Loading...';
	$ctrl.commits = [];
	$ctrl.branches = [];
	$ctrl.tags = [];
	$ctrl.repository = collectionService.commit.repository;
	
	$ctrl.init = function(commitId) {
		$ctrl.commits.clear();
		$ctrl.commits.push(commitId);
		$ctrl.selected = commitId;
	};
	
	
	$ctrl.switchCommit = function(sha) {
		collectionService.commit.commitId = sha;
		$http({
		    method: 'PUT',
			url: '/rest/commit',
			data: collectionService.commit
		}).then(
			function(resp) {
				$ctrl.selected = sha;
				$uibModalInstance.close();
			}, 
			function(d) {
				Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
			}
		);
	};

	$ctrl.loadCommitList = function() {
		$ctrl.loading=true;
		$http({
			method: 'POST',
			url: '/rest/commits',
			data: {repository: $ctrl.repository, page: $ctrl.page}
		}).then(
				function(resp) {
					$ctrl.commits.clear();
					resp.data.forEach(function(item, index, array) {
						$ctrl.commits.push(item);
					});
				}, 
				function(d) {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				})['finally'](function() {
					$ctrl.loading=false;
				});
	};
	
	$ctrl.loadBranchList = function() {
		$ctrl.loading=true;
		$http({
			method: 'POST',
			url: '/rest/branches',
			data: {repository: $ctrl.repository}
		}).then(
				function(resp) {
					$ctrl.branches.clear();
					resp.data.forEach(function(item, index, array) {
						$ctrl.branches.push(item);
					});
				}, 
				function(d) {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				})['finally'](function() {
					$ctrl.loading=false;
				});
	};
	
	$ctrl.loadTagList = function() {
		$ctrl.loading=true;
		$http({
			method: 'POST',
			url: '/rest/tags',
			data: {repository: $ctrl.repository}
		}).then(
				function(resp) {
					$ctrl.tags.clear();
					resp.data.forEach(function(item, index, array) {
						$ctrl.tags.push(item);
					});
				}, 
				function(d) {
					Notification.error('Failed with ['+ d.status + '] '+ d.statusText);
				})['finally'](function() {
					$ctrl.loading=false;
				});
	};
	
	$ctrl.cancel = function() {
		$uibModalInstance.close();
	};
	
	$ctrl.loadNextCommitPage = function() {
		$ctrl.page++;
		$ctrl.loadCommitList();
	};

	$ctrl.loadPrevCommitPage = function() {
		$ctrl.page--;
		$ctrl.loadCommitList();
	};
	
	$ctrl.loadBranchList();
	$ctrl.loadTagList();
	$ctrl.loadCommitList();
	
	
}]);