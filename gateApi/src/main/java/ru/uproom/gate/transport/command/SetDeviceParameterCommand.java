package ru.uproom.gate.transport.command;

import ru.uproom.gate.transport.dto.DeviceDTO;

/**
 * Created by osipenko on 09.09.14.
 */
public class SetDeviceParameterCommand extends Command {

    private static final long serialVersionUID = -1274353867412672286L;
    private DeviceDTO device;

    public SetDeviceParameterCommand(DeviceDTO device) {
        super(CommandType.SetDeviceParameter);
        this.device = device;
    }

    public DeviceDTO getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "SetDeviceParameterCommand{" +
                "device=" + device +
                '}';
    }
}
