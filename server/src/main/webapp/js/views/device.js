/**
 * Created by HEDIN on 05.01.2015.
 */
define(['exports', 'backbone', 'handlebars'
    ], function (exports, Backbone) {

        function switchChange () {
            console.log("Click on device");
            this.model.switch();
            this.model.save();
        }

        function changeLevel() {
            this.model.setLevel(this.$('[data-id=level]').val());
            this.model.save();
        }

        function render () {
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

        // TODO try to extract describing object as function;
        var DeviceView = Backbone.View.extend({
            events: {
                'switch-change [data-id=switchCheck]': 'sendDevice',
                'slideStop [data-id=level]': 'sendLevel'
            },

            sendDevice: switchChange,

            sendLevel: changeLevel,

            initialize: function (options) {
                Backbone.View.prototype.initialize.call(this, options);
                this.template = options.template;
                // Catch model change event
                this.model.on('change', render, this);
            },

            render: render,

            tagName: 'tr'
        })

        _.extend(exports, {
            View:DeviceView
        });

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
