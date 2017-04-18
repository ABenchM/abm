angular.module('de.fraunhofer.abm').factory("commitSelectorService", ['$uibModal', function($uibModal) {
	return function() {
		var modalInstance = $uibModal.open({
			animation : true,
			ariaLabelledBy : 'modal-title',
			ariaDescribedBy : 'modal-body',
			templateUrl : 'template/modalCommitSelector.htm',
			controller : 'commitSelectionController',
			controllerAs : '$ctrl',
			size: 'lg'
		});
	};
}]);