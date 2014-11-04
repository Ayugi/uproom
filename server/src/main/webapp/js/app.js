define(['backbone', 'views', 'models', 'slider'], function(Backbone, Views, Models) { // , 'slider' , Slider
	var
		devicesCollection = new Models.DevicesCollection(),
		app = (new Views.DevicesView({})).render();

	// Reset list with empty collection it will catch add/change event
	app.layout.list.reset(devicesCollection);

	// WARN Query backend for user session state here
	app.layout.auth.setCallback({success: function(user) {
		app.user = user;

//        if(user.id) {
            // In case of successfull authoization — fetch data
        devicesCollection.fetch();
        app.layout.accountMenu.reset(user);

        // And prepare UI
        app.activate();

//        } else
//            console.log("auth failed - user-id = 0");

	}});

    app.layout.auth.setCallback({fail: function(user) {

        console.log("auth failed - no such user");
        $('#error-msg').text("Неправильное имя пользвателя или пароль");
        $('#error-msg').show();

    }});
})
				