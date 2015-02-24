/**
 * Created by HEDIN on 09.01.2015.
 */
define(['exports', 'backbone','hbs!../../../templates/scene-list','js/views/baseV.js', 'handlebars'], function (exports, Backbone, SceneListTpl, Base) {
    _.extend(exports, {
        View: Base.View.extend({
            events: {
                /*'switch-change [data-id=switchCheck]': 'sendDevice',
                 'slideStop [data-id=level]': 'sendLevel',
                 'changeColor [data-id=colorPicker]': 'changeColor'*/
            },

            template: SceneListTpl
        })});
    function render() {
        console.log("scenes render");
    }
})
