package ru.uproom.prsistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.uproom.domain.ColorScene;
import ru.uproom.domain.User;
import ru.uproom.service.GateSocketHandler;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by HEDIN on 08.01.2015.
 */
@Service
@Transactional
public class SceneDaoImpl implements SceneDao {
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(SceneDaoImpl.class);

    // TODO check if adding device to scene will work with merge.
    @Override
    public ColorScene saveScene(ColorScene scene, int userId) {
        scene.setUser(entityManager.find(User.class, userId));
        if (scene.getId() == 0)
            entityManager.persist(scene);
        else
            scene = entityManager.merge(scene);

        return scene;
    }

    @Override
    public ColorScene removeScene(int userId, int sceneId) {
        ColorScene scene = entityManager.find(ColorScene.class, sceneId);
        if (null != scene && scene.getUser().getId() == userId)
            entityManager.remove(scene);
        return scene;
    }

    @Override
    public List<ColorScene> fetchUserScenes(int userId) {
        User user = new User();
        user.setId(userId);
        return entityManager.createNamedQuery("userScenes").setParameter("user", user).getResultList();
    }

    @Override
    public ColorScene fetchUserScene(int userId, int sceneId) {
        ColorScene scene = entityManager.find(ColorScene.class, sceneId);
        if (null == scene || null == scene.getUser() || scene.getUser().getId() != userId)
            return null;
        scene.prepareDeviceIds();
        return scene;
    }
}
