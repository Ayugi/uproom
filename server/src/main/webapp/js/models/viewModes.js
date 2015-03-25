/**
 * Created by hedin on 09.01.2015.
 */
define(['exports', 'backbone'], function (exports, Backbone) {

    exports.Model = Backbone.Model.extend({
        getActiveFrame: function(){
            return this.get('activeFrame');
        },
        setActiveFrame: function (frame) {
            this.set('activeFrame', frame);
        }
    });
});
