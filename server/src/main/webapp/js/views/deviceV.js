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
                    'change [data-id=switchCheck]': 'switchChange',
                    'change [data-id=level]': 'sendLevel',
                    'changeColor [data-id=colorPicker]': 'changeColor',
                    'change [data-id=sceneSelect]': 'sceneSelect'
                },

                switchChange: switchChange,
                sendLevel: changeLevel,
                changeColor: changeColor,
                initialize: initialize,
                render: render,
                update: update,
                templatePostProcessing: templatePostProcessing,
                sceneSelect: sceneSelect,
                tagName: 'div',

                renderedFlag: false
            }),
            isDeviceViewable: function (type) {
                return deviceTypesToTemplates[type]
            }
        });

        function sceneSelect(event) {
            this.selectDeviceForScene(this.model.id, event.target.checked)
        }

        // ------------- functional code --------------
        function switchChange(event) {
            console.log("Click on device");
            this.model.setState(event.currentTarget.checked);
            this.model.save();
        }

        function changeLevel() {
            console.log("changeLevel");
            this.model.setLevel(Math.round(this.$('[data-id=level]').val()));
            this.model.save();
            $("#sliderValue" + this.model.id).html(this.model.getLevel() + "%");
        }

        function render() {
            console.log("render" + arguments + " renderedFlag " + this.renderedFlag);
            var model = this.model;
            $('#switch' + model.id).empty();
            this.$el.html(this.template({
                state: model.getState() ? "checked" : "",
                name: model.getName(),
                id: model.id,
                value: model.getLevel(),
                zid: model.getZId(),
                color: '#' + model.getColor().toString(16),
                selectScene: "newScene" == this.mode
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

            //$('#switch' + this.model.id).bootstrapSwitch('setState', this.model.getState());
            if (this.slider)
                this.slider.val(parseInt(this.model.getLevel()));
            //if ($('#switch' + this.model.id)[0].checked != this.model.getState())
            $('#switch' + this.model.id)[0].checked = this.model.getState();
            $("#sliderValue" + this.model.id).html(this.model.getLevel() + "%");
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
            console.log(Backbone.View.prototype.initialize);
            this.template = deviceTypesToTemplates[options.type];
            this.mode = options.mode;
            this.selectDeviceForScene = options.selectDeviceForScene;
        }

        function hidePicker(model) {

        }

        function showPicker(model) {

        }

        function templatePostProcessing(model) {
            var deviceView = this;
            if ($('#slider' + model.id)[0]) {
                $('#slider' + model.id).each(function () {
                    var isStart = $(this).data('is-start');

                    deviceView.slider = $(this).noUiSlider({
                        start: isStart,
                        range: {
                            'min': 0,
                            'max': 100
                        }
                    });
                });
            }

            /*

             if($('#slider' + model.id)) {
             $('#slider' + model.id).Link('lower').to($('#s_value'), null, wNumb({
             decimals: 0,
             postfix: '%',
             }));
             // $('#theslider').Link('lower').to($('#s_value'));
             }


             /*
             this.slider = $('#slider' + model.id).slider({
             formatter: function (value) {
             return 'Current value: ' + value;
             }
             });*/

            console.log("model.getColor().toString(16) " + model.getColor().toString(16));
            if (model.get("type") == "Rgbw") {
                var colorPicker = Raphael.colorwheel($("#colorPickerContainer" + model.id), 300, 180)
                    .color("#" + model.getColor().toString(16));
                deviceView.colorPicker = colorPicker;

                // colorPicker.onchange(function (color) {
                colorPicker.ondrag(null, function (color) {
                    var colors = [parseInt(color.r), parseInt(color.g), parseInt(color.b)];
                    console.log("colorPicker.onchange", color);
                    $("#colorDisplay" + model.id).css("background", color.hex);
                    $("#colorDisplay" + model.id).html("RGB: "+color.hex);
                    model.setColor(parseInt(color.r) * 256 * 256 + parseInt(color.g) * 256 + parseInt(color.b));
                    model.save();
                });
                /*
                function hide() {
                    $("#colorPickerContainer" + model.id).hide();
                    var pickerSwitch = $("#colorPickerSwitch" + model.id);
                    pickerSwitch.on('click', show);
                    pickerSwitch.removeClass("fa-chevron-up");
                    pickerSwitch.addClass("fa-chevron-down");
                }

                function show() {
                    $("#colorPickerContainer" + model.id).show();
                    var pickerSwitch = $("#colorPickerSwitch" + model.id);
                    pickerSwitch.on('click', hide);
                    pickerSwitch.removeClass("fa-chevron-down");
                    pickerSwitch.addClass("fa-chevron-up");
                }
                  */
                //$("#colorPickerSwitch" + model.id).on('click', hide());

                //$("#nav-video").on();
            }


            /*
             $(function () {
             $('#picker' + model.id).colorpicker();
             });*/

            //this.switch = $('#switch' + model.id).bootstrapSwitch();

            /*
             function onEdit(response, newValue) {
             model.setName(newValue)
             model.save();
             }

             $('#devicename' + model.id).editable({
             success: onEdit
             });*/
        }
    })
