/**
 * Created by HEDIN on 09.01.2015.
 */
define(['exports', 'backbone', 'hbs!../../../templates/scene-list', 'js/views/baseV.js', 'handlebars'], function (exports, Backbone, SceneListTpl, Base) {
    _.extend(exports, {
        View: Base.View.extend({
            events: {
                /*'switch-change [data-id=switchCheck]': 'sendDevice',
                 'slideStop [data-id=level]': 'sendLevel',
                 'changeColor [data-id=colorPicker]': 'changeColor'*/
            },
            reset: reset,
            add: add,
            clear: clear,
            template: SceneListTpl
        })
    });

    function add(model) {
        if ($.inArray(model.id, this.rendered)>=0)
            return;
        this.rendered.push( model.id);
        console.log("add: function (model) ", model);

        if (!model.get("type") || !Device.isDeviceViewable(model.get("type")))
            return this;

        var deviceView = new Device.View({
            model: model, type: model.get("type") })

        this.layout.items = this.layout.items.concat(deviceView)
        addItem(deviceView.el);

        deviceView.render();

        return this;
    }

    function reset(collection) {

        this.collection = collection;
        this.rendered = [];

        // Create new item once collection gets new model
        this.collection.off('add', this.add, this);
        this.collection.on('add', this.add, this);

        this.clear();
        this.collection.each(this.add, this);

        return this;
    }

    function clear() {
        _.invoke(this.layout.items, 'remove');
        this.layout.items = [];
        return this
    }


})
