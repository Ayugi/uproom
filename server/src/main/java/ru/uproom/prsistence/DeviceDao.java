package ru.uproom.prsistence;

import ru.uproom.domain.Device;

import java.util.List;

/**
 * Created by HEDIN on 16.09.2014.
 */
public interface DeviceDao {
    Device saveDevice(Device device);

    List<Device> fetchUserDevices(Integer userId);
}
