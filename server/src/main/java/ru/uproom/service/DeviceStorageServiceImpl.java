package ru.uproom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.uproom.domain.Device;
import ru.uproom.prsistence.DeviceDao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HEDIN on 16.09.2014.
 */
@Service
public class DeviceStorageServiceImpl implements DeviceStorageService{
    private Map <Integer, UserDeviceStorage> userStorage = new HashMap<>();

    @Autowired
    private DeviceDao deviceDao;

    @Override
    public void onNewUser(int userId) {
        UserDeviceStorage storage = new UserDeviceStorage(deviceDao);
        userStorage.put(userId, storage);
        storage.addDevices(deviceDao.fetchUserDevices(userId));
    }

    @Override
    public void addDevices(int userId, List<Device> devices) {
        userStorage.get(userId).addDevices(devices);
    }

    @Override
    public Collection<Device> fetchDevices(int userId) {
        return userStorage.get(userId).fetchDevices();
    }
}
