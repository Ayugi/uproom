/**
 * Created by HEDIN on 25.02.2015.
 */
define(['exports', 'backbone', 'hbs!../../../templates/devices-list', 'js/views/baseV.js', 'js/views/deviceV.js',
        'handlebars'],
    function (exports, Backbone, DevicesListTpl, Base, Device) {
        _.extend(exports, {
            View: Base.View.extend({
                events: {
                    'click [id=add-device-btn]': 'sendAddDevice',
                    'click [id=add-scene-finish-btn]': 'sendAddScene'


                },
                sendAddDevice: sendAddDevice,
                sendAddScene: sendAddScene,
                rendered: [],
                add: add,
                clear: clear,
                reset: reset,
                template: DevicesListTpl,
                render: render ,
                initialize: initialize
            })
        });

        var _this = this.View;

        function sendAddDevice() {
            console.log("IN sendAddDevice");
            console.log("$=", $);
            console.log("$.ajax=", $.ajax);

            $.ajax(DEVICES_URL + "/add");

            $("#gateStatusSpan")[0].innerHTML = "test";
            $.ajax({
                type: "GET",
                url: "http://localhost:8080/rest/devices/status",

                success: function (data) {
                    $("#gateStatusSpan")[0].innerHTML = data;
                }
            });

        }

        function sendAddScene(event){
            console.log("sendAddScene " , _this.selectedDevices);
            if (!_this.selectedDevices)
                return;
            this.sceneCollection.create({name: 'new', deviceIds: _this.selectedDevices});
            _this.selectedDevices = null;
            this.modeChange("scenes","");
        }

        function selectDeviceForScene(id, selecet){
            if (selecet)
                _this.selectedDevices.push(id);
            else
                _this.selectedDevices.splice(_this.selectedDevices.indexOf(id),1);
            console.log(id  + " " + selecet, _this.selectedDevices);
        }

        function render() {
            console.log("render device list " + this.mode);
            var model = this.model;
            this.$el.html(this.template({
                //sceneSelect: "newScene" == this.mode
            }));
        }

        function add(model) {
            if ($.inArray(model.id, this.rendered) >= 0)
                return;
            this.rendered.push(model.id);
            console.log("add: function (model) ", model);

            if (!model.get("type") || !Device.isDeviceViewable(model.get("type"))
                || model.attributes.zid ==2 || model.attributes.zid == 3)
                return this;

            var deviceView = new Device.View({
                model: model,
                type: model.get("type"),
                mode: this.mode ,
                selectDeviceForScene: selectDeviceForScene});

            this.layout.items = this.layout.items.concat(deviceView);
            addItem(deviceView.el);

            deviceView.render();

            return this;
        }

        // Append actual html element to the DOM
        function addItem(el) {
            $('[data-id=list-container]').append(el);
            return this
        }

        function clear() {
            _.invoke(this.layout.items, 'remove');
            this.layout.items = [];
            return this
        }

        function reset(collection, modeChange) {
            this.modeChange = modeChange;
            console.log("this.prototype.initialize" + this.__proto__.initialize);

            this.collection = collection;
            this.rendered = [];

            // Create new item once collection gets new model
            this.collection.off('add', this.add, this);
            this.collection.on('add', this.add, this);

            this.clear();
            this.collection.each(this.add, this);

            return this;
        }

        function initialize(options) {
            this.__proto__.initialize(options);
            //Backbone.View.prototype.initialize.call(this, options);
            this.mode = options.mode;
            if ("newScene" == this.mode)
                _this.selectedDevices = [];
            this.sceneCollection = options.sceneCollection;
        }

    }
);