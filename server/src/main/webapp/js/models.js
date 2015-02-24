define(['exports', 'backbone', 'js/models/deviceM.js', 'js/models/viewModes.js'],
    function (exports, Backbone, Device, ViewModes) {

        //exports.DeviceModel = Device.Model;

        exports.DevicesCollection = Backbone.Collection.extend({
            model: Device.Model,
            url: DEVICES_URL
        });

        exports.ViewModesModel = ViewModes.Model;

    });