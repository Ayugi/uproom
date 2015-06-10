/**
 * Created by HEDIN on 05.01.2015.
 */
define(['exports', 'backbone', 'hbs!../../../templates/scene', 'handlebars'],
    function (exports, Backbone, scene) {
        // ------------- Backbone definition -------------
        _.extend(exports, {
            View: Backbone.View.extend({
                events: {
                    'click [data-id=delete]': 'deleteScene'
                },
                deleteScene: deleteScene,
                template: scene,
                initialize: initialize,
                render: render,
                update: update,
                templatePostProcessing: templatePostProcessing,
                tagName: 'tr',

                renderedFlag: false
            }),
            isDeviceViewable: function (type) {
                return deviceTypesToTemplates[type]
            }
        });
        // ------------- functional code --------------

        function deleteScene() {
            console.log("deleteScene" + this.model.id);
            this.model.destroy();
        }

        function render() {
            console.log("render" + arguments + " renderedFlag " + this.renderedFlag);
            var model = this.model;
            this.$el.html(this.template({
                name: model.getName(),
                id: model.id,
            }));

            this.$el.data('id', model.id);

            this.templatePostProcessing(model);
            var _this = this;

            function callUpdate() {
                _this.update();
            }

            model.off('change');
            model.on('change', callUpdate);
            this.renderedFlag = true;
            return this;
        }

        function update() {
            console.log("update renderedFlag " + this.renderedFlag);
        }

        function initialize(options) {
            Backbone.View.prototype.initialize.call(this, options);
        }

        function templatePostProcessing(model) {
            function onEdit(response, newValue) {
                model.setName(newValue)
                model.save();
            }

            $('#scenename' + model.id).editable({
                success: onEdit
            });
        }
    }
);
