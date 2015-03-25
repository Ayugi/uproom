package ru.uproom.service;

import ru.uproom.domain.ColorScene;
import ru.uproom.domain.Device;

import java.util.Collection;
import java.util.List;

/**
 * Created by HEDIN on 16.09.2014.
 */
public interface DeviceStorageService {
    void onNewUser(int userId);

    void addDevices(int userId, List<Device> devices);

    Collection<Device> fetchDevices(int userId);

    Device fetchDevice(int userId, int deviceId);

    Device updateDevice(int userId, Device device);

    void applyScene(int userId, ColorScene scene);
}
