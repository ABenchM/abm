angular.module('de.fraunhofer.abm').factory("buildViewerService", ['$uibModal', function($uibModal) {
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
		builds: [],
		initialSelection: undefined
	};
}]);