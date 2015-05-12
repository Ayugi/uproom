/**
 * Created by HEDIN on 09.01.2015.
 */
define(['exports', 'backbone', 'hbs!../../../templates/scene-list', 'js/views/baseV.js', 'js/views/sceneV.js',
        'js/models/sceneM.js', 'handlebars'],
    function (exports, Backbone, SceneListTpl, Base, Scene, SceneModel) {
        _.extend(exports, {
            View: Base.View.extend({
                events: {
                    'click [id=add-scene-btn]': 'addscene'

                    /*'switch-change [data-id=switchCheck]': 'sendDevice',
                     'slideStop [data-id=level]': 'sendLevel',
                     'changeColor [data-id=colorPicker]': 'changeColor'*/
                },
                addscene: addScene,
                reset: reset,
                add: add,
                destroy:destroy,
                clear: clear,
                template: SceneListTpl
            })
        });

        function addScene(){
            //var newScene = this.collection.create({name: 'new'});
            this.modeChange("devices","newScene");
            //add(newScene);
        }

        function add(model) {
            console.log("scene add rendered model", this.rendered, model.id);
            if (!model.id)
                return;
            if ($.inArray(model.id, this.rendered) >= 0)
                return;
            this.rendered.push(model.id);

            var sceneView = new Scene.View({model: model})

            this.layout.items = this.layout.items.concat(sceneView)
            addItem(sceneView.el);

            sceneView.render();

            return this;
        }

        function destroy(model){
            console.log("on collection model destroy" + model.id);
        }

        function addItem(el) {
            $('[data-id=list-container]').append(el);
        }

        function reset(collection, modeChange) {
            console.log("reset" );
            this.modeChange = modeChange;
            this.collection = collection;
            this.rendered = [];

            // Create new item once collection gets new model
            this.collection.off('add', this.add, this);
            this.collection.off('destroy', this.destroy, this);
            this.collection.on('add', this.add, this);
            this.collection.on('destroy', this.destroy, this);

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
