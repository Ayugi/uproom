package ru.uproom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.uproom.domain.ColorScene;
import ru.uproom.domain.ColorSceneDeviceParam;
import ru.uproom.domain.Device;
import ru.uproom.gate.transport.dto.DeviceState;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.DeviceType;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;
import ru.uproom.prsistence.DeviceDao;

import java.util.*;

/**
 * Created by HEDIN on 16.09.2014.
 */
@Service
public class DeviceStorageServiceImpl implements DeviceStorageService {
    private static final Logger LOG = LoggerFactory.getLogger(GateSocketHandler.class);
    private Map<Integer, UserDeviceStorage> userStorage = new HashMap<>();

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private GateTransport gateTransport;

    @Override
    public void onNewUser(int userId) {
        if (userStorage.containsKey(userId))
            return;
        UserDeviceStorage storage = new UserDeviceStorage(deviceDao, userId);
        userStorage.put(userId, storage);
        storage.addDevices(deviceDao.fetchUserDevices(userId));
    }

    @Override
    public void addDevices(int userId, List<Device> devices) {
        LOG.info("addDevices userId " + userId + " devices " + devices);
        userStorage.get(userId).addDevices(devices);
    }

    @Override
    public Collection<Device> fetchDevices(int userId) {
        Collection<Device> devices = userStorage.get(userId).fetchDevices();
        if (devices.isEmpty()) {
            Device test = new Device();
            test.setName("test device");
            test.getParameters().put(DeviceParametersNames.Unknown, "test");
            test.setUser(SessionHolderImpl.getInstance().currentUser());
            test.setZid(-1);
            test.setState(DeviceState.On);
            //deviceDao.saveDevice(test);
            devices.add(test);
            addDevices(userId, Collections.singletonList(test));
        }
        /*if (userStorage.get(userId).getDeviceById(2) == null) {
            Device rgb = new Device();
            rgb.setType(DeviceType.Rgbw);
            rgb.setName("RGB");
            rgb.getParameters().put(DeviceParametersNames.Color, 0);
            rgb.setId(2);
            devices.add(rgb);

            userStorage.get(userId).addDevices(Collections.singletonList(rgb));
        }*/
        return devices;
    }

    @Override
    public Device fetchDevice(int userId, int deviceId) {
        return userStorage.get(userId).getDeviceById(deviceId);
    }

    @Override
    public Device updateDevice(int userId, Device device) {
        Device stored = userStorage.get(userId).getDeviceById(device.getId());
        if (!stored.getName().equals(device.getName())) {
            stored.setName(device.getName());
            deviceDao.saveDevice(stored, userId);
        }
        if (handleParams(device, stored))
            gateTransport.sendCommand(new SetDeviceParameterCommand(stored.toDto()), userId);

        return stored;
    }

    @Override
    public void applyScene(int userId, ColorScene scene) {
        UserDeviceStorage userDeviceStorage = userStorage.get(userId);
        Set<Device> affectedDevices = new HashSet<>();
        for (ColorSceneDeviceParam param : scene.getDeviceParams()) {
            Device device = userDeviceStorage.getDeviceById(param.getDeviceId());
            device.getParameters().put(param.getParametersName(),
                    param.restoreParamValueObject());
            affectedDevices.add(device);
        }
        for (Device device : affectedDevices)
            gateTransport.sendCommand(new SetDeviceParameterCommand(device.toDto()), userId);
    }

    private boolean handleParams(Device device, Device stored) {
        boolean paramsChanged = false;
        for (Map.Entry<DeviceParametersNames, Object> entry : device.getParameters().entrySet()) {
            if (!stored.getParameters().containsKey(entry.getKey())
                    || !stored.getParameters().get(entry.getKey()).equals(entry.getValue())) {
                stored.getParameters().put(entry.getKey(), entry.getValue());
                paramsChanged = true;
            }
        }
        return paramsChanged;
    }
}
