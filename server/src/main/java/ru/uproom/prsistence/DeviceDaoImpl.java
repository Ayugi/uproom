package ru.uproom.prsistence;

import org.springframework.stereotype.Service;
import ru.uproom.domain.Device;
import ru.uproom.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HEDIN on 16.09.2014.
 */
@Service
@Transactional
public class DeviceDaoImpl implements DeviceDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Device saveDevice(Device device) {
        //device.setUser(entityManager.find(User.class, device.getUser().getId()));
        if (device.getId() == 0)
            entityManager.persist(device);
        else
            return entityManager.merge(device);
        return device;
    }

    @Override
    public List<Device> fetchUserDevices(Integer userId) {
        User user = new User();
        user.setId(userId);
        return entityManager.createNamedQuery("userDevices").setParameter("user", user)
               .getResultList();
    }
}
