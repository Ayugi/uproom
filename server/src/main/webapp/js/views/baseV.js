define(['exports', 'backbone', 'handlebars'], function (exports, Backbone) {
    _.extend(exports, {
        View: Backbone.View.extend({
            initialize: function (options) {
                Backbone.View.prototype.initialize.call(this, options);
                //this.app = options.app;
                this.layout = {};
            },

            render: function () {
                this.$el.html(this.template());
                return this
            }
        })
    });

});
