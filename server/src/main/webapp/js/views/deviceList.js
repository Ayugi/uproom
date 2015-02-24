/**
 * Created by HEDIN on 25.02.2015.
 */
define(['exports', 'backbone', 'hbs!../../../templates/devices-list', 'js/views/baseV.js', 'js/views/deviceV.js',
        'handlebars'],
    function (exports, Backbone, DevicesListTpl, Base, Device) {
        _.extend(exports, {
            View: Base.View.extend({
                events: {
                    'click [id=add-device-btn]': 'sendAddDevice'
                },

                sendAddDevice: function () {
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

                },

                add: function (model) {
                    console.log("add: function (model) ", model);

                    if (!model.get("type"))
                        return this;

                    var deviceView = new Device.View({
                        model: model, type: model.get("type") })

                    this.layout.items = this.layout.items.concat(deviceView)
                    this.addItem(deviceView.el);

                    deviceView.render();

                    return this;
                },

                // Append actual html element to the DOM
                addItem: function (el) {
                    this.$('[data-id=list-container]').append(el);
                    return this
                },

                // Clear internal items storage and DOM structure
                clear: function () {
                    _.invoke(this.layout.items, 'remove');
                    this.layout.items = [];
                    return this
                },

                // List item view

                reset: function (collection) {
                    this.collection = collection;

                    // Create new item once collection gets new model
                    this.collection.on('add', this.add, this);

                    this.clear();
                    this.collection.each(this.add, this);

                    return this;
                },

                template: DevicesListTpl
            })
        });
    }
);