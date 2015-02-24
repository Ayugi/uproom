define(['exports', 'backbone', 'js/views/baseV.js', 'js/views/accountMenu.js', 'js/views/auth.js', 'js/views/deviceList.js',
        'js/views/scene.js','hbs!../../../templates/sidebar', 'handlebars', ],
    function (exports, Backbone, Base, AccountMenu, Auth, DevicesList,
              Scene, SidebarTpl) {
        var SidebarView = Base.View.extend({template: SidebarTpl});
        _.extend(exports, {
            View: Base.View.extend({
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
                    Base.View.prototype.initialize.call(this, options);
                    this.user = {};
                    this.model = options.model;
                    this.frameChange = options.frameChange;
                },

                render: function () {
                    var model = this.model;
                    this.layout.accountMenu = (new AccountMenu.View({app: this, el: '#fat-menu'})).render();
                    this.layout.auth = (new Auth.View({el: '#auth'})).render();
                    $('#main-frame').empty();
                    if (model.getActiveFrame() == 'devices')
                        this.layout.list = (new DevicesList.View({el: '#main-frame'})).render();
                    else
                        this.layout.list = (new Scene.View({el: '#main-frame'})).render();
                    this.layout.sidebar = (new SidebarView({el: '#sidebar'})).render();
                    var frameChange = this.frameChange;
                    // TODO this looks ugly, consider how to make it better
                    $("#nav-devices").on('click', function () {
                        frameChange('devices')
                    });
                    $("#nav-scenes").on('click', function () {
                        frameChange('scenes')
                    });

                    return this;
                }
            })
        });

    }
);

