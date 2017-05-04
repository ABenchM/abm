angular.module('de.fraunhofer.abm').factory("modalLoginService", ['$uibModal', function($uibModal) {
	return function() {
		var modalInstance = $uibModal.open({
			animation : true,
			ariaLabelledBy : 'modal-title',
			ariaDescribedBy : 'modal-body',
			templateUrl : 'template/modalLogin.htm',
			controller : 'modalLoginController',
			controllerAs : '$ctrl',
		});
	};
}]);