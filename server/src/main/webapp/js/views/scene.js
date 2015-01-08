/**
 * Created by HEDIN on 09.01.2015.
 */
define(['exports', 'backbone', 'handlebars'], function (exports, Backbone) {
    _.extend(exports, {View: Backbone.View.extend({
        events: {
            /*'switch-change [data-id=switchCheck]': 'sendDevice',
            'slideStop [data-id=level]': 'sendLevel',
            'changeColor [data-id=colorPicker]': 'changeColor'*/
        },

        render: render,

        template: "DevicesListTpl"
    })});
    function render(){

    }
})
