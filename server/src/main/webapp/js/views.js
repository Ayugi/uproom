define([
    'exports', 'backbone', 'hbs!../templates/account-menu',
    'hbs!../templates/auth', 'hbs!../templates/devices-list', 'hbs!../templates/sidebar',
     'js/views/deviceV.js', 'js/views/scene.js',
    'handlebars'
], function (exports, Backbone, AccountMenuTpl,
             AuthTpl, DevicesListTpl, SidebarTpl,
              Device, Scene) {

    var BaseView = Backbone.View.extend({
        initialize: function (options) {
            Backbone.View.prototype.initialize.call(this, options);
            this.app = options.app;
            this.layout = {};
        },

        render: function () {
            this.$el.html(this.template());
            return this
        }
    });

    _.extend(exports, {
        AccountMenuView: BaseView.extend({
            events: {'click [data-id=signout]': function (event) {
                this.app.deactivate();
                $('body').trigger('click'); // Hide account menu
                return false;
            }},

            reset: function (user) {
                this.$('[data-id=name]').html(user.name);
                return this
            },
            template: AccountMenuTpl
        }),

        // Get username/password and pass to backend for validation
        AuthView: BaseView.extend({
            events: {
                'keypress [data-id=username]': 'submitOnEnter',
                'keypress [data-id=password]': 'submitOnEnter',
                'click [data-id=submit]': 'processAuth'
            },

            processAuth: function () {
                // Call backend auth method
                // WARN Change .get() to .post() when work with real backend
                var jx = $.get(AUTH_URL, {
                    password: this.$('[data-id=password]').val(),
                    username: this.$('[data-id=username]').val()
                }, (function (data) {
                    //                    this.callback[data.success ? 'success' : 'fail'](data) }).bind(this), 'json');
                    this.callback[data.id > 0 ? 'success' : 'fail' ](data)
                }).bind(this), 'json');

                jx.done(function () {
                    console.log("auth got reply from server");
                });
                jx.fail(function () {
                    console.log("auth network error");
                    $('#error-msg').text("Ошибка сети");
                    $('#error-msg').show();

                });
                jx.always(function () {
                    console.log("auth ended");
                });
                return false;
            },

            submitOnEnter: function (e) {
                if (e.keyCode == 13) this.processAuth();
            },

            // Define callbacks to be called on auth try
            setCallback: function (funcs) {
                this.callback = this.callback || {};
                _.extend(this.callback = this.callback || {}, funcs);
                return this;
            },

            template: AuthTpl
        }),

        // Manage application core views
        MainView: BaseView.extend({
            activate: function () {
                this.layout.auth.$el.hide();

                _.invoke([
                    this.layout.accountMenu.$el,
                    this.layout.list.$el,
                    this.layout.sidebar.$el
                ], 'show');

                return this;
            },

            deactivate: function () {
                this.layout.auth.$el.show();

                _.invoke([
                    this.layout.accountMenu.$el,
                    this.layout.list.$el,
                    this.layout.sidebar.$el
                ], 'hide');

                return this;
            },

            initialize: function (options) {
                BaseView.prototype.initialize.call(this, options);
                this.user = {};
            },

            render: function () {
                var model = this.model;
                this.layout.accountMenu = (new exports.AccountMenuView({app: this, el: '#fat-menu'})).render();
                this.layout.auth = (new exports.AuthView({el: '#auth'})).render();
                this.layout.list = (new exports.DeviceView({el: '#main-frame'})).render();
                this.layout.sidebar = (new exports.SidebarView({el: '#sidebar'})).render();

                return this;
            }
        }),

        SceneView: Scene.View,

        // List of devices itself
        DeviceView: BaseView.extend(
            {
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
            }),

        SidebarView: BaseView.extend({template: SidebarTpl})
    })
})
