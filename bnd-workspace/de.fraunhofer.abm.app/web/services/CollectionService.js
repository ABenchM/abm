angular.module('de.fraunhofer.abm').factory('collectionService', function() {
	return {
		collection: undefined,
		version: {},
		commit: {},
		toCreate: [],
		toAdd: [],
		singleSelect: undefined,

		setCollection: function (collection) {
			this.collection = collection;
			this.version = collection.versions[0];
		},
		
		getCollection: function () {
			return this.collection;
		}
	};
});