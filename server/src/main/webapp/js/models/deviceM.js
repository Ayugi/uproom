/**
 * Created by hedin on 09.01.2015.
 */
define(['exports', 'backbone'], function (exports, Backbone) {

    exports.Model = Backbone.Model.extend({
        getName: function () {
            return this.get('name');
        },
        setName: function (name) {
            this.set('name', name);
        },
        getZId: function () {
            return this.get('zid');
        },
        switch: function () {
            var p = this.parameters();
            console.log("switch" + p);

            var newState = !this.getState();
            console.log("switch new state " + newState)
            if (!newState) {
                this.set("levelBackup", p.Level)
                p.Level = 0;
            }
            else {
                var levelBackup = this.get("levelBackup");
                if (levelBackup)
                    p.Level = levelBackup;
            }
            this.setState(newState);
            console.log("state after " + this.get("state"));
        },
        setState: function (st) {
            var p = this.parameters();
            p.Switch = st;

            if ("MultilevelSwitch" == this.get("type"))
                if (st) {
                    var levelBackup = this.get("levelBackup");
                    if (levelBackup)
                        p.Level = levelBackup;
                    else
                        p.Level = 100;
                } else {
                    this.set("levelBackup", p.Level)
                    p.Level = 0;
                }
            if ("Rgbw" == this.get("type"))
                if (st) {
                    var colorBackup = this.get("colorBackup");
                    if (colorBackup)
                        p.Color = colorBackup;
//                    else
//                        p.Color = 100;
                } else {
                    this.set("colorBackup", p.Level)
                    p.Color = 0;
                }
            this.trigger("change", "state");
        },
        getState: function () {
            console.log("getState");
            var p = this.parameters();
            if (!p.Switch)
                p.Switch = false;
            return p.Switch;
        },
        getStateFlag: function () {
            console.log("getStateFlag " + this.getState());
            return this.getState();
        },
        parameters: function () {
            var p = this.get("parameters");
            if (!p) {
                p = {};
                this.set("parameters", p);
            }
            return p;
        },
        setLevel: function (level) {
            // TODO remove duplication
            if (level > 0 && !this.getStateFlag())
                this.setState(true)

            if (level == 0 && this.getStateFlag())
                this.setState(false)

            this.parameters().Level = level;

            this.trigger("change", "level");
        },

        getLevel: function () {
            // TODO remove duplication
            var p = this.parameters();
            if (!p.Level)
                p.Level = 0;
            return p.Level;
        },

        setColor: function (color) {
            if (color > 0 && !this.getStateFlag())
                this.setState(true)
            if (color == 0 && this.getStateFlag())
                this.setState(false)

            this.parameters().Color = color;
        },

        getColor: function () {
            if (!this.parameters().Color)
                return 0;
            return  this.parameters().Color;
        },

        url: DEVICES_URL

    });
});
