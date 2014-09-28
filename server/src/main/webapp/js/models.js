define(['exports', 'backbone'], function(exports, Backbone) {
	exports.DeviceModel = Backbone.Model.extend({
		getTitle: function() { return '*' + this.get('name') }
	});

	exports.DevicesCollection = Backbone.Collection.extend({
		model: exports.DeviceModel,
		url: DEVICES_URL
	});
});