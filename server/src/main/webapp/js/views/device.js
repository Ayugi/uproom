/**
 * Created by HEDIN on 05.01.2015.
 */
define(['exports', 'backbone', 'handlebars'
    ], function (exports, Backbone) {
        // ------------- Backbone definition --------------
        _.extend(exports, {View: Backbone.View.extend({
            events: {
                'switch-change [data-id=switchCheck]': 'sendDevice',
                'slideStop [data-id=level]': 'sendLevel'
            },

            sendDevice: switchChange,
            sendLevel: changeLevel,
            initialize: initialize,
            render: render,
            tagName: 'tr'
        })});
        // ------------- functional code --------------
        function switchChange() {
            console.log("Click on device");
            this.model.switch();
            this.model.save();
        }

        function changeLevel() {
            this.model.setLevel(this.$('[data-id=level]').val());
            this.model.save();
        }

        function render() {
            var model = this.model;
            this.$el.html(this.template({
                state: model.getState() == "On" ? "checked" : "",
                name: model.getName(),
                id: model.id,
                value: model.getLevel(),
                zid: model.getZId()
            }));

            this.$el.data('id', model.id);

            templatePostProcessing(model);

            return this;
        }

        function initialize(options) {
            Backbone.View.prototype.initialize.call(this, options);
            this.template = options.template;
            this.model.on('change', render, this);
        }

        function templatePostProcessing(model) {
            $('#slider' + model.id).slider({
                formatter: function (value) {
                    return 'Current value: ' + value;
                }
            });

            $(function () {
                $('.picker' + model.id).colorpicker();
            });

            $('#switch' + model.id).bootstrapSwitch();

            function onEdit(response, newValue) {
                model.setName(newValue)
                model.save();
            }

            $('#devicename' + model.id).editable({
                success: onEdit
            });
        }
    }
)
