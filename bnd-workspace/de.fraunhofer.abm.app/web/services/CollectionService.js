angular.module('de.fraunhofer.abm').factory('collectionService', function() {
	return {
		collection: {},
		version: {},
		commit: {},

		setCollection: function (collection) {
			this.collection = collection;
			this.version = collection.versions[0];
		},
		
		getCollection: function () {
			return this.collection;
		}
	};
});