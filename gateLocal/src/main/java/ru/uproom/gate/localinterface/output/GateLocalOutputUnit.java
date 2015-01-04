package ru.uproom.gate.localinterface.output;

import ru.uproom.gate.transport.dto.DeviceDTO;

import java.util.List;

/**
 * Created by osipenko on 29.12.14.
 */
public interface GateLocalOutputUnit {

    public void setListDTO(List<DeviceDTO> devices);

    public void stop();

}
