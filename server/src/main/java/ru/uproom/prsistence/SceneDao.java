package ru.uproom.prsistence;

import ru.uproom.domain.ColorScene;
import ru.uproom.domain.Device;

import java.util.List;

/**
 * Created by HEDIN on 08.01.2015.
 */
public interface SceneDao {
    ColorScene saveScene(ColorScene scene, int userId);

    ColorScene removeScene(int userId, int sceneId);

    List<ColorScene> fetchUserScenes(int userId);

    ColorScene fetchUserScene(int userId, int sceneId);
}
