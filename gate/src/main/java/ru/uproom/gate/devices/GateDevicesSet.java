package ru.uproom.gate.devices;

import ru.uproom.gate.devices.zwave.ZWaveDeviceParametersNames;
import ru.uproom.gate.transport.ServerTransport;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

import java.util.List;

/**
 * Created by osipenko on 28.09.14.
 */
public interface GateDevicesSet {

    public boolean isReady();

    public void setReady(boolean ready);

    public void setFailed(boolean failed);

    public long getHomeId();

    public void setHomeId(long homeId);

    public void setControllerState(DeviceStateEnum state, boolean clearRequest);

    public DeviceStateEnum getControllerState();

    public List<DeviceDTO> getDeviceDTOList();

    public void setDeviceDTO(DeviceDTO dto);

    public void addGateDevice(int index);

    public void removeGateDevice(int index);

    public void addDeviceGroup(int indexDevice, int indexGroup);

    public void addGateDeviceParameter(int indexDevice, ZWaveDeviceParametersNames paramName, Object paramValue);

    public void removeGateDeviceParameter(int indexDevice, ZWaveDeviceParametersNames paramName);

    public void requestMode(DeviceStateEnum mode);

    public DeviceStateEnum getRequestedMode();

    public ServerTransport getTransport();

}
