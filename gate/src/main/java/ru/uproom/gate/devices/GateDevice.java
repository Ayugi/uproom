package ru.uproom.gate.devices;

import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

/**
 * Created by osipenko on 28.09.14.
 */
public interface GateDevice {

    public int getZId();

    public Object getParameter(DeviceParametersNames name);

    public Object setParameter(DeviceParametersNames name, Object value);

    public boolean isExistGroup(Integer index);

    public void setGroup(Integer index);

    public void removeGroup(Integer index);

    public DeviceDTO getDeviceDTO();

}
