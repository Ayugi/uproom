define(['exports', 'backbone'], function (exports, Backbone) {
    exports.DeviceModel = Backbone.Model.extend({
        getTitle: function () {
            console.log("getTitle");
            return '*' + this.get('name') + "(" + this.get('zid') + ")"
        },
        switch: function () {
            console.log("switch: function () {");

//            console.log("state " +  this.get("state"));

            console.log("THIS in DeviceModel = ");
            console.log(this);


            console.log("parameters IN MODEL = ");
            var p = this.get("parameters");
            console.log(p);
            console.log(p.State);


//            console.log("set " +  this.set );

            this.setState(this.getState() == "On" ? "Off" : "On");
            console.log("state after " + this.get("state"));
        },
        setState: function (st) {
            console.log("setState");
            // TODO remove duplication
            var p = this.get("parameters");
            if (!p) {
                p = {};
                this.set("parameters", p);
            }
            p.State = st;
        },
        getState: function () {
            console.log("getState");
            var p = this.get("parameters");
            if (!p) {
                p = {};
                this.set("parameters", p);
            }
            if (!p.State)
                p.State = "Off"
            return p.State;
        },
        getStateFlag: function () {
            console.log("getStateFlag " + this.getState() == "On");
            return this.getState() == "On";
        },
        url: DEVICES_URL

    });

    exports.DevicesCollection = Backbone.Collection.extend({
        model: exports.DeviceModel,
        url: DEVICES_URL
    });
});