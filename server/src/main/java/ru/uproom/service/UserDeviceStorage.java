package ru.uproom.service;

import ru.uproom.domain.Device;
import ru.uproom.prsistence.DeviceDao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by HEDIN on 16.09.2014.
 */
public class UserDeviceStorage {
    private DeviceDao deviceDao;
    private Map<Integer, Device> devicesById;
    private Map<Integer, Device> devicesByZId;

    public UserDeviceStorage(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public void addDevices(List<Device> devices) {
        for (Device device : devices) {
            if (devicesById.containsKey(device.getId())) {
                Device existing = devicesById.get(device.getId());
                existing.mergeById(device);
                deviceDao.saveDevice(existing);
                continue;
            }
            if (0 == device.getId()) {
                device.setName("new device");
                deviceDao.saveDevice(device);
            }
            devicesById.put(device.getId(), device);
            if (device.getZid() > 0)
                devicesByZId.put(device.getZid(), device);
        }
    }

    public Collection<Device> fetchDevices() {
        return devicesById.values();
    }

    public Device getDeviceById(int id){
        return devicesById.get(id);

    }
}