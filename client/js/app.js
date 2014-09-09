define(['backbone', 'views', 'models'], function(Backbone, Views, Models) {
	var
		devicesCollection = new Models.DevicesCollection(),
		app = (new Views.DevicesView()).render();

	// Reset list with empty collection it will catch add/change event
	app.layout.list.reset(devicesCollection);

	// WARN Query backend for user session state here
	app.layout.auth.setCallback({success: function(user) {
		app.user = user;

		// In case of successfull authoization — fetch data
		devicesCollection.fetch();
		app.layout.accountMenu.reset(user);
						
		// And prepare UI
		app.activate();
	}});
})
				