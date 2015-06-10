define(['exports', 'backbone', 'js/views/baseV.js', 'js/views/accountMenu.js', 'js/views/auth.js', 'js/views/deviceList.js',
        'js/views/sceneList.js', 'hbs!../../../templates/sidebar', 'hbs!../../../templates/video', 'handlebars', ],
    function (exports, Backbone, Base, AccountMenu, Auth, DevicesList, SceneList, SidebarTpl, VideoTpl) {
        var SidebarView = Base.View.extend({template: SidebarTpl});

        var VideoView = Base.View.extend({template: VideoTpl});
        _.extend(exports, {
            View: Base.View.extend({
//                activate: function () {
//                    this.layout.auth.$el.hide();
//
//                    _.invoke([
//                        this.layout.accountMenu.$el,
//                        this.layout.list.$el,
//                        this.layout.sidebar.$el
//                    ], 'show');
//
//                    return this;
//                },
//
//                deactivate: function () {
//                    this.layout.auth.$el.show();
//
//                    _.invoke([
//                        this.layout.accountMenu.$el,
//                        this.layout.list.$el,
//                        this.layout.sidebar.$el
//                    ], 'hide');
//
//                    return this;
//                },

                initialize: function (options) {
                    Base.View.prototype.initialize.call(this, options);
                    this.user = {};
                    this.model = options.model;
                    this.frameChange = options.frameChange;
                    this.authSuccess = options.authSuccess;
                },

                render: function () {
                    var _this = this;
                    function modeChange(frame,mode){
                        console.log("modeChange" + _this.model.modes.getActiveFrame());
                        _this.model.modes.setMode(mode);
                        _this.frameChange(frame);
                    }
//                    this.layout.accountMenu = (new AccountMenu.View({app: this, el: '#fat-menu'})).render();
//                    this.layout.auth = (new Auth.View({el: '#auth'})).render();
                    $('#sidebar').empty();
                    $('#container').empty();
                    if (this.model.modes.getActiveFrame() == 'auth') {
                        this.layout.list = new Auth.View({el: '#container', authSuccess: this.authSuccess});
                        this.layout.list.render();
                    } else if (this.model.modes.getActiveFrame() == 'devices') {
                        console.log("devices mode " + this.model.modes.getMode());
                        this.layout.list = new DevicesList.View({el: '#container',
                            mode: this.model.modes.getMode(),
                            sceneCollection: this.model.scenes});
                        this.layout.list.render();
                        this.layout.list.reset(this.model.devices, modeChange);
                        this.model.devices.fetch();
                    }
//                    else if (this.model.modes.getActiveFrame() == 'scenes') {//
//                        this.layout.list = new SceneList.View({el: '#main-frame'});
//                        this.layout.list.render();
//                        this.layout.list.reset(this.model.scenes, modeChange);
//                        this.model.scenes.fetch();
//                    } else {
//                        this.layout.list = (new VideoView({el: '#main-frame'})).render();
//                    }

                    this.layout.sidebar = (new SidebarView({el: '#sidebar'})).render();

//                    var frameChange = this.frameChange;
//                    // TODO this looks ugly, consider how to make it better
//                    $("#nav-devices").on('click', function () {
//                        frameChange('devices')
//                    });
//                    $("#nav-scenes").on('click', function () {
//                        frameChange('scenes')
//                    });
//                    $("#nav-video").on('click', function () {
//                        frameChange('video')
//                    });
                    return this;
                }
            })
        });

    }
);

