define(
    ['backbone', 'views', 'models', 'slider', 'editable', 'bootstrap_switch', 'colorpicker' ],
    function (Backbone, Views, Models) {
        $.fn.editable.defaults.mode = 'inline';
        var devicesCollection = new Models.DevicesCollection();
        //var testModel = Backbone.Model.extend({url: &quot;&quot;});
        //var test = new testModel();

        var modes = new Models.ViewModesModel();
        modes.setActiveFrame('devices');

        var mainView = (new Views.MainView({model: modes, frameChange: frameChange})).render();

        // Reset list with empty collection it will catch add/change event
        mainView.layout.list.reset(devicesCollection);

        // WARN Query backend for user session state here
        mainView.layout.auth.setCallback({success: function (user) {
            mainView.user = user;

            devicesCollection.fetch();
            mainView.layout.accountMenu.reset(user);

            // And prepare UI
            mainView.activate();

        }});

        mainView.layout.auth.setCallback({fail: function (user) {
            console.log("auth failed - no such user");
            $('#error-msg').text("Неправильное имя пользвателя или пароль");
            $('#error-msg').show();

        }});

        function frameChange(mode){
            modes.setActiveFrame(mode);
            mainView.render();
        }

        $("#nav-devices").on('click',function(){frameChange('devices')});
        $("#nav-scenes").on('click',function(){frameChange('scenes')});
    })
				