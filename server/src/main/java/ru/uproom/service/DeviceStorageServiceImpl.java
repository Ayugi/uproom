package ru.uproom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.uproom.domain.Device;
import ru.uproom.gate.transport.command.SendDeviceListCommand;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;
import ru.uproom.prsistence.DeviceDao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HEDIN on 16.09.2014.
 */
@Service
public class DeviceStorageServiceImpl implements DeviceStorageService {
    private Map<Integer, UserDeviceStorage> userStorage = new HashMap<>();

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private GateTransport gateTransport;

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
        Collection<Device> devices = userStorage.get(userId).fetchDevices();
        if (devices.isEmpty()){
            Device test = new Device();
            test.setName("test device");
            test.getParameters().put(DeviceParametersNames.Unknown,"test");
            test.getParameters().put(DeviceParametersNames.ApplicationVersion,"on");
            test.setUser(SessionHolderImpl.getInstance().currentUser());
            test.setZid(-1);
            deviceDao.saveDevice(test);
            devices.add(test);
        }
        return devices;
    }

    @Override
    public Device updateDevice(int userId, Device device) {
        Device stored = userStorage.get(userId).getDeviceById(device.getId());
        if (!stored.getName().equals(device.getName())) {
            stored.setName(device.getName());
            deviceDao.saveDevice(stored);
        }

        if (handleParams(device, stored))
            gateTransport.sendCommand(new SetDeviceParameterCommand(stored.toDto()),userId);

        return stored;
    }

    private boolean handleParams(Device device, Device stored) {
        boolean paramsChanged = false;
        for (Map.Entry<DeviceParametersNames,String> entry : device.getParameters().entrySet()){
            if (!stored.getParameters().containsKey(entry.getKey())
                    || !stored.getParameters().get(entry.getKey()).equals(entry.getValue())){
                stored.getParameters().put(entry.getKey(), entry.getValue());
                paramsChanged = true;
            }
        }
        return paramsChanged;
    }
}
