// add a clear() method for arrays
Array.prototype.clear = function() {
	while (this.length) {
		this.pop();
	}
};