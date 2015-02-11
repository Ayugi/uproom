define(['exports', 'backbone', 'js/models/deviceM.js'],
    function (exports, Backbone, Device) {
        exports.DeviceModel = Device.Model;

        exports.DevicesCollection = Backbone.Collection.extend({
            model: exports.DeviceModel,
            url: DEVICES_URL
        });
    });