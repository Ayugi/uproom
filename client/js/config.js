requirejs.config({
	baseUrl: 'js',

	paths: {
		backbone  : '../components/backbone/backbone',
		handlebars: '../components/handlebars/handlebars.min',
		hbs       : '../components/require-handlebars-plugin/hbs',
		jquery    : '../components/jquery/dist/jquery.min',
		underscore: '../components/underscore/underscore-min'
	},

	hbs: {templateExtension: 'html'},

	shim: {
		backbone: {
			deps: ['jquery', 'underscore'],
			exports: 'Backbone'
		},

		handlebars: {exports: 'Handlebars'},
		underscore: {exports: '_'}
	}
});