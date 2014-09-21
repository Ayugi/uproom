package ru.uproom.service;

import ru.uproom.domain.Device;

import java.util.List;

/**
 * Created by HEDIN on 16.09.2014.
 */
public interface DeviceStorageService {
    void onNewUser(int userId);

    void addDevices(int userId, List<Device> devices);

    java.util.Collection<Device> fetchDevices(int userId);
}
