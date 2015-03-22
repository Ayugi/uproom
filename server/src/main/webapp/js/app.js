define(
    ['backbone', 'views', 'models', 'slider', 'editable', 'bootstrap_switch', 'colorpicker' ],
    function (Backbone, Views, Models) {
        $.fn.editable.defaults.mode = 'inline';

        var modelInstances = prepareModelInstances();

        var mainView = (new Views.MainView({model: modelInstances, frameChange: frameChange})).render();

        // Reset list with empty collection it will catch add/change event
        mainView.layout.list.reset(modelInstances.devices);

        // Reset list with empty collection it will catch add/change event
        mainView.layout.list.reset(modelInstances.scenes);

        // WARN Query backend for user session state here
        mainView.layout.auth.setCallback({success: function (user) {
            mainView.user = user;

            //modelInstances.devices.fetch();
            //modelInstances.scenes.fetch();
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
            modelInstances.modes.setActiveFrame(mode);
            mainView.render();
        }

        $("#nav-devices").on('click',function(){frameChange('devices')});
        $("#nav-scenes").on('click',function(){frameChange('scenes')});

        function prepareModelInstances() {
            var devices = new Models.DevicesCollection();
            var scenes = new Models.ScenesCollection();
            //var testModel = Backbone.Model.extend({url: &quot;&quot;});
            //var test = new testModel();

            var modes = new Models.ViewModesModel();
            modes.setActiveFrame('devices');

            return {devices: devices, scenes : scenes, modes:modes};
        }
    })
				