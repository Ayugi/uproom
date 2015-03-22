/**
 * Created by HEDIN on 05.01.2015.
 */
define(['exports', 'backbone', 'hbs!../../../templates/rgbw', 'hbs!../../../templates/dimmer',
        'hbs!../../../templates/switch',
        'handlebars'],
    function (exports, Backbone, RgbwTemplate, DimmerTpl, SwitchTpl) {
        var deviceTypesToTemplates = {
            MultilevelSwitch: DimmerTpl,
            BinarySwitch: SwitchTpl,
            BinarySensor: "senson_binary",
            MultilevelSensor: "sensor_analog",
            Rgbw: RgbwTemplate
        }

        // ------------- Backbone definition -------------
        _.extend(exports, {View: Backbone.View.extend({
            events: {
                'switch-change [data-id=switchCheck]': 'sendDevice',
                'slideStop [data-id=level]': 'sendLevel',
                'changeColor [data-id=colorPicker]': 'changeColor'
            },

            sendDevice: switchChange,
            sendLevel: changeLevel,
            changeColor: changeColor,
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
                zid: model.getZId(),
                color: '#' + model.getColor().toString(16)
            }));

            this.$el.data('id', model.id);

            templatePostProcessing(model);

            return this;
        }

        function changeColor(ev) {
            var color = ev.color.toHex();
            var colorAsInt = parseInt(color.substring(1), 16);
            console.log("change color" + color + " " + colorAsInt)
            this.model.setColor(colorAsInt);
            this.model.save();
        }


        function initialize(options) {
            Backbone.View.prototype.initialize.call(this, options);
            this.template = deviceTypesToTemplates[options.type];
            //options.template;
            this.model.on('change', render, this);
        }

        function templatePostProcessing(model) {
            $('#slider' + model.id).slider({
                formatter: function (value) {
                    return 'Current value: ' + value;
                }
            });

            /*
            $(function () {
                $('#picker' + model.id).colorpicker();
            });*/

            $('#switch' + model.id).bootstrapSwitch();

            function onEdit(response, newValue) {
                model.setName(newValue)
                model.save();
            }

            $('#devicename' + model.id).editable({
                success: onEdit
            });
        }
    })
