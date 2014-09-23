package ru.uproom.gate.transport.command;

import ru.uproom.gate.transport.dto.DeviceDTO;

/**
 * Created by osipenko on 09.09.14.
 */
public class SetDeviceParameterCommand extends Command {

    private DeviceDTO device;

    public SetDeviceParameterCommand(DeviceDTO device) {
        super(CommandType.SetDeviceParameter);
        this.device = device;
    }

    public DeviceDTO getDevice() {
        return device;
    }
}
