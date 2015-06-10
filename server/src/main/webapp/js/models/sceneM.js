define(['exports', 'backbone'], function (exports, Backbone) {

    exports.Model = Backbone.Model.extend({
        getName: function () {
            return this.get('name');
        },
        setName: function (name) {
            this.set('name', name);
        }
//        ,        url: SCENES_URL

    });
});
