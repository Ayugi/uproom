/**
 * Created by HEDIN on 25.02.2015.
 */
define(['exports', 'backbone', 'hbs!../../../templates/auth', 'js/views/baseV.js', 'handlebars'],
    function (exports, Backbone, AuthTpl, Base) {
        _.extend(exports, {
            View: Base.View.extend({
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

                initialize: function (options) {
                    Base.View.prototype.initialize.call(this, options);
                    this.callback = {};
                    this.callback.success = options.authSuccess;
                },

                render: render,

                template: AuthTpl
            })
        });

        function render() {
            console.log("render auth ");
            this.$el.html(this.template({}));
            inputDecoration();
        }

        function inputDecoration(){
            //Add blue animated border and remove with condition when focus and blur
            if($('.fg-line')[0]) {
                $('body').on('focus', '.form-control', function(){
                    $(this).closest('.fg-line').addClass('fg-toggled');
                })

                $('body').on('blur', '.form-control', function(){
                    var p = $(this).closest('.form-group');
                    var i = p.find('.form-control').val();

                    if (p.hasClass('fg-float')) {
                        if (i.length == 0) {
                            $(this).closest('.fg-line').removeClass('fg-toggled');
                        }
                    }
                    else {
                        $(this).closest('.fg-line').removeClass('fg-toggled');
                    }
                });
            }
        }
    }
);