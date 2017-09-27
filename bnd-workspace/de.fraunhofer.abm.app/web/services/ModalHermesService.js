angular.module('de.fraunhofer.abm').factory("modalHermesService", ['$uibModal', function($uibModal) {
	return{
		launch: function() {
			var modalInstance = $uibModal.open({
				animation : true,
				ariaLabelledBy : 'modal-title',
				ariaDescribedBy : 'modal-body',
				templateUrl : 'template/modalHermes.htm',
				controller : 'modalHermesController',
				controllerAs : '$ctrl',
			});
		},
		version: undefined,
		collection : undefined
	};
}]);