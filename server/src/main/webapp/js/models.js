define(['exports', 'backbone'], function (exports, Backbone) {
    exports.DeviceModel = Backbone.Model.extend({
        getTitle: function () {
            return '*' + this.get('name')
        },
        switch: function () {
            console.log("switch: function () {");
            console.log("get " +  this.get );
            console.log("state " +  this.get("state"));

            console.log("set " +  this.set );

            this.set("state",this.get("state") == "On" ? "Off" : "On");
            console.log("state after " +  this.get("state"));
        },
        setState: function (st) {
            this.state = st;
        },
        url: DEVICES_URL

    });

    exports.DevicesCollection = Backbone.Collection.extend({
        model: exports.DeviceModel,
        url: DEVICES_URL
    });
});