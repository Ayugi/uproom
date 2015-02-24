define([
    'exports', 'backbone', 'js/views/main.js',
    'handlebars'
], function (exports, Backbone, Main) {
    // TODO consider some common view functionality to add here, or removing the file.
    _.extend(exports, {
        // Manage application core views
        MainView: Main.View
    })
})
