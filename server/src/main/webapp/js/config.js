requirejs.config({
    baseUrl: 'js',

    paths: {
        backbone            : '../components/backbone/backbone',
        handlebars          : '../components/handlebars/handlebars.min',
        hbs                 : '../components/require-handlebars-plugin/hbs',
        jquery              : '../components/jquery/dist/jquery.min',
        underscore          : '../components/underscore/underscore-min',
        slider              : '../components/seiyria-bootstrap-slider/js/bootstrap-slider',
        editable            : '../components/x-editable/dist/bootstrap-editable/js/bootstrap-editable',
        bootstrap_popover   : '../components/bootstrap/js/bootstrap-popover',
        bootstrap_tooltip   : '../components/bootstrap/js/bootstrap-tooltip',
         bootstrap_switch    : '../components/bootstrap-switch/build/js/bootstrap-switch'
    },

    hbs: {templateExtension: 'html'},

    shim: {
        backbone: {
            deps: ['jquery', 'underscore'],// 'jquery',
            exports: 'Backbone'
        },

        handlebars: {exports: 'Handlebars'},
        underscore: {exports: '_'},
        slider: ['jquery'],
        editable : ['jquery','bootstrap_popover'],
        bootstrap_popover: ['jquery','bootstrap_tooltip'],
        bootstrap_tooltip: ['jquery'],
        bootstrap_switch:   ['jquery']
    }
});