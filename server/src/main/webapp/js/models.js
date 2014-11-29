define(['exports', 'backbone'], function (exports, Backbone) {
    exports.DeviceModel = Backbone.Model.extend({
        getName: function () {
            return this.get('name');
        },
        setName: function (name) {
            this.set('name', name);
        },
        getZId: function(){
            return this.get('zid');
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

            var st = this.getState() == "On" ? "Off" : "On";
            if ("Off" == st) {
                this.set("levelBackup", p.Level)
                p.Level = 0;
            }
            else {
                var levelBackup = this.get("levelBackup");
                if (levelBackup)
                    p.Level = levelBackup;
            }
            this.setState(st);
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
        setLevel: function (level) {
            // TODO remove duplication
            var p = this.get("parameters");
            if (!p) {
                p = {};
                this.set("parameters", p);
            }
            if (level > 0 && !this.getStateFlag())
                this.setState("On")

            if (level == 0 && this.getStateFlag())
                this.setState("Off")

            p.Level = level;
        },

        getLevel: function () {
            // TODO remove duplication
            var p = this.get("parameters");
            if (!p) {
                p = {};
                this.set("parameters", p);
            }
            if (!p.Level)
                p.Level = 0;
            return p.Level;
        },

        url: DEVICES_URL

    });

    exports.DevicesCollection = Backbone.Collection.extend({
        model: exports.DeviceModel,
        url: DEVICES_URL
    });
});