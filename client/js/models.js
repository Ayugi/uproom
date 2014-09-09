var
	DeviceModel = Backbone.Model.extend({
		getTitle: function() { return '$' + this.get('name') }
	}),

	DevicesCollection = Backbone.Collection.extend({
		model: DeviceModel,
		url: DEVICES_URL
	});