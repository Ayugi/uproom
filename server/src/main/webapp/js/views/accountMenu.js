define(['exports', 'backbone', 'hbs!../../../templates/account-menu', 'js/views/baseV.js', 'handlebars'],
    function (exports, Backbone, AccountMenuTpl, Base) {
        _.extend(exports, {
            View: Base.View.extend({
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
            })
        });
    }
);