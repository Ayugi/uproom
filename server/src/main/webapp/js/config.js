requirejs.config({
    baseUrl: 'js',

    paths: {
        backbone: '../components/backbone/backbone',
        handlebars: '../components/handlebars/handlebars.min',
        hbs: '../components/require-handlebars-plugin/hbs',
        jquery: '../components/jquery/dist/jquery.min',
        jquery_nicescroll: '../components/jquery.nicescroll/jquery.nicescroll.min',
        underscore: '../components/underscore/underscore-min',
        waves: '../components/waves/dist/waves.min',
        nouislider: '../components/nouislider/distribute/jquery.nouislider.min',
        raphael             : '../components/raphael/raphael',
        colorwheel          : '../components/colorwheel/colorwheel'

//        slider              : '../components/seiyria-bootstrap-slider/js/bootstrap-slider',
//        editable            : '../components/x-editable/dist/bootstrap-editable/js/bootstrap-editable',
//        bootstrap_popover   : '../components/bootstrap/js/bootstrap-popover',
//        bootstrap_tooltip   : '../components/bootstrap/js/bootstrap-tooltip',
//        bootstrap_switch    : '../components/bootstrap-switch/build/js/bootstrap-switch',
        //colorpicker         : '../components/mjolnic-bootstrap-colorpicker/dist/js/bootstrap-colorpicker',
        //baseView            : 'views/baseV'
    },

    hbs: {templateExtension: 'html'},

    shim: {
        backbone: {
            deps: ['jquery', 'underscore'],// 'jquery',
            exports: 'Backbone'
        },
        jquery_nicescroll: ['jquery'],
        functions: ['jquery', 'jquery_nicescroll', 'waves'],
        handlebars: {exports: 'Handlebars'},
        underscore: {exports: '_'},
        slider: ['jquery'],
        editable: ['jquery', 'bootstrap_popover'],
        nouislider: ['jquery'],
//        bootstrap_popover:  ['jquery','bootstrap_tooltip'],
//        bootstrap_tooltip:  ['jquery'],
//        bootstrap_switch:   ['jquery'],
        colorwheel      :   ['raphael'],
        raphael         :   ['jquery']
        //baseView:           ['Backbone']
    }
});