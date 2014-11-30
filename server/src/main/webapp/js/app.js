define(
    ['backbone', 'views', 'models', 'slider', 'editable'],
    function (Backbone, Views, Models) {
        $.fn.editable.defaults.mode = 'inline';
        var devicesCollection = new Models.DevicesCollection();
        var app = (new Views.DevicesView({})).render();

        // Reset list with empty collection it will catch add/change event
        app.layout.list.reset(devicesCollection);

        // WARN Query backend for user session state here
        app.layout.auth.setCallback({success: function (user) {
            app.user = user;

            devicesCollection.fetch();
            app.layout.accountMenu.reset(user);

            // And prepare UI
            app.activate();

        }});

        app.layout.auth.setCallback({fail: function (user) {

            console.log("auth failed - no such user");
            $('#error-msg').text("Неправильное имя пользвателя или пароль");
            $('#error-msg').show();

        }});
    })
				