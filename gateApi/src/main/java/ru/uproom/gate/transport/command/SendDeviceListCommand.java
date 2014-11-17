package ru.uproom.gate.transport.command;

import ru.uproom.gate.transport.dto.DeviceDTO;

import java.util.List;

/**
 * Created by osipenko on 10.09.14.
 */
public class SendDeviceListCommand extends Command {
    private static final long serialVersionUID = -574703015426721812L;
    List<DeviceDTO> devices;

    public SendDeviceListCommand(List<DeviceDTO> devices) {
        super(CommandType.SendDeviceList);
        this.devices = devices;
    }

    public List<DeviceDTO> getDevices() {
        return devices;
    }
}
