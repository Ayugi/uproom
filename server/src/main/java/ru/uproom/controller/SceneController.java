package ru.uproom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.uproom.domain.ColorScene;
import ru.uproom.domain.ColorSceneDeviceParam;
import ru.uproom.domain.Device;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;
import ru.uproom.prsistence.SceneDao;
import ru.uproom.service.DeviceStorageService;
import ru.uproom.service.SessionHolder;
import ru.uproom.service.SessionHolderImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by HEDIN on 08.01.2015.
 * TODO consider separation of scene manager
 */
@Controller
@RequestMapping(value = "scenes")
public class SceneController {

    private SessionHolder sessionHolder = SessionHolderImpl.getInstance();

    private static final Logger LOG = LoggerFactory.getLogger(SceneController.class);

    @Autowired
    private DeviceStorageService storageService;

    @Autowired
    private SceneDao sceneDao;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Collection<ColorScene> listScenes() {
        List<ColorScene> colorScenes = sceneDao.fetchUserScenes(sessionHolder.currentUserId());
        if (colorScenes.isEmpty()) {
            ColorScene test = new ColorScene();
            test.setName("test");
            colorScenes.add(test);
            sceneDao.saveScene(test, sessionHolder.currentUserId());
        }
        for (ColorScene scene : colorScenes)
            scene.prepareDeviceIds();
        return colorScenes;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{id}")
    @ResponseBody
    public ColorScene updateScene(@RequestBody ColorScene scene, @PathVariable int id) {
        scene.setDeviceParams(prepareSceneParams(scene));

        sceneDao.saveScene(scene, sessionHolder.currentUserId());

        return scene;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ColorScene addScene(@RequestBody ColorScene scene) {
        scene.setDeviceParams(prepareSceneParams(scene));

        sceneDao.saveScene(scene, sessionHolder.currentUserId());

        return scene;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE})
    @ResponseBody
    public ColorScene deleteScene( @PathVariable int id) {
        return sceneDao.removeScene(sessionHolder.currentUserId(),id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "apply/{id}")
    public void applyScene(@PathVariable int id) {
        ColorScene scene = sceneDao.fetchUserScene(sessionHolder.currentUserId(), id);
        storageService.applyScene(sessionHolder.currentUserId(), scene);
    }

    private ArrayList<ColorSceneDeviceParam> prepareSceneParams(ColorScene scene) {
        ArrayList<ColorSceneDeviceParam> params = new ArrayList<ColorSceneDeviceParam>();
        for (Integer deviceId : scene.getDeviceIds()) {
            Device device = storageService.fetchDevice(sessionHolder.currentUserId(), deviceId);
            for (Map.Entry<DeviceParametersNames, Object> entry : device.getParameters().entrySet())
                params.add(new ColorSceneDeviceParam(
                        deviceId, entry.getKey(),
                        String.valueOf(entry.getValue())
                ));
        }
        return params;
    }

}
