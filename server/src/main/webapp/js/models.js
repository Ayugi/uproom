define(['exports', 'backbone', 'js/models/deviceM.js', 'js/models/viewModes.js', 'js/models/sceneM.js'],
    function (exports, Backbone, Device, ViewModes, Scene) {

        //exports.DeviceModel = Device.Model;

        exports.DevicesCollection = Backbone.Collection.extend({
            model: Device.Model,
            url: DEVICES_URL
        });

        exports.ViewModesModel = ViewModes.Model;

        exports.ScenesCollection = Backbone.Collection.extend({
            model: Scene.Model,
            url: SCENES_URL
        });


    });