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
            //BinarySensor: "senson_binary",
            //MultilevelSensor: "sensor_analog",
            Rgbw: RgbwTemplate
        }

        // ------------- Backbone definition -------------
        _.extend(exports, {
            View: Backbone.View.extend({
                events: {
                    'switch-change [data-id=switchCheck]': 'switchChange',
                    'slideStop [data-id=level]': 'sendLevel',
                    'changeColor [data-id=colorPicker]': 'changeColor'
                },

                switchChange: switchChange,
                sendLevel: changeLevel,
                changeColor: changeColor,
                initialize: initialize,
                render: render,
                tagName: 'tr'
            }),
            isDeviceViewable: function (type) {
                return deviceTypesToTemplates[type]
            }
        });
        // ------------- functional code --------------
        function switchChange(event, state) {
            console.log("Click on device");
            this.model.setState(state.value);
            this.model.save();
            this.render();
        }

        function changeLevel() {
            this.model.setLevel(this.$('[data-id=level]').val());
            this.model.save();
        }

        function render() {
            var model = this.model;
            $('#switch' + model.id).empty();
            this.$el.html(this.template({
                state: model.getState() ? "checked" : "",
                name: model.getName(),
                id: model.id,
                value: model.getLevel(),
                zid: model.getZId(),
                color: '#' + model.getColor().toString(16)
            }));

            this.$el.data('id', model.id);

            templatePostProcessing(model);

            model.on('change', this.render);
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

        function hidePicker(model){

        }

        function showPicker(model){

        }

        function templatePostProcessing(model) {
            $('#slider' + model.id).slider({
                formatter: function (value) {
                    return 'Current value: ' + value;
                }
            });

            console.log("model.getColor().toString(16) " + model.getColor().toString(16));
            if (model.get("type") == "Rgbw") {
                var colorPicker = Raphael.colorwheel($("#colorPickerContainer" + model.id), 250, 180)
                    .color("#" + model.getColor().toString(16));


                // colorPicker.onchange(function (color) {
                colorPicker.ondrag(null, function (color) {
                    var colors = [parseInt(color.r), parseInt(color.g), parseInt(color.b)];
                    console.log("colorPicker.onchange", color);
                    $("#colorDisplay" + model.id).css("background", color.hex);
                    model.setColor(parseInt(color.r) * 256 * 256 + parseInt(color.g) * 256 + parseInt(color.b));
                    model.save();
                });

                function hide(){
                    $("#colorPickerContainer" + model.id).hide();
                    var pickerSwitch = $("#colorPickerSwitch" + model.id);
                    pickerSwitch.on('click',show);
                    pickerSwitch.removeClass("fa-chevron-up");
                    pickerSwitch.addClass("fa-chevron-down");
                }

                function show(){
                    $("#colorPickerContainer" + model.id).show();
                    var pickerSwitch = $("#colorPickerSwitch" + model.id);
                    pickerSwitch.on('click',hide);
                    pickerSwitch.removeClass("fa-chevron-down");
                    pickerSwitch.addClass("fa-chevron-up");
                }

                $("#colorPickerSwitch" + model.id).on('click',hide());

                $("#nav-video").on();
            }


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
