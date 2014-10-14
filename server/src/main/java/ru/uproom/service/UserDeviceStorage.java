package ru.uproom.service;

import ru.uproom.domain.Device;
import ru.uproom.prsistence.DeviceDao;

import java.util.*;

/**
 * Created by HEDIN on 16.09.2014.
 */
public class UserDeviceStorage {
    private DeviceDao deviceDao;
    private int userId;
    private Map<Integer, Device> devicesById = new HashMap<>();
    private Map<Integer, Device> devicesByZId = new HashMap<>();

    public UserDeviceStorage(DeviceDao deviceDao, int userId) {
        this.deviceDao = deviceDao;
        this.userId = userId;
    }

    public void addDevices(List<Device> devices) {
        for (Device device : devices) {
            if (devicesById.containsKey(device.getId())) {
                Device existing = devicesById.get(device.getId());
                existing.mergeById(device);
                deviceDao.saveDevice(existing, userId);
                continue;
            }
            if (0 == device.getId()) {
                device.setName("new device");
                deviceDao.saveDevice(device, userId);
            }
            devicesById.put(device.getId(), device);
            if (device.getZid() > 0)
                devicesByZId.put(device.getZid(), device);
        }
    }

    public Collection<Device> fetchDevices() {
        return new ArrayList<>(devicesById.values());
    }

    public Device getDeviceById(int id) {
        return devicesById.get(id);

    }
}