angular.module('de.fraunhofer.abm').factory("hermesViewerService", ['$uibModal', function($uibModal) {
	return{
		launch: function() {
			var modalInstance = $uibModal.open({
				animation : true,
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'template/modalHermesViewer.htm',
				controller : 'hermesViewerController',
				controllerAs : '$ctrl',
			});
		},
		version: undefined,
		collection : undefined
	};
}]);