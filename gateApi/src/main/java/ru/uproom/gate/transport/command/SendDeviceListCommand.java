package ru.uproom.gate.transport.command;

import ru.uproom.gate.transport.dto.DeviceDTO;

import java.util.List;

/**
 * Created by osipenko on 10.09.14.
 */
public class SendDeviceListCommand extends Command {
    List<DeviceDTO> devices;
    private String gateId;

    public SendDeviceListCommand(String gateId, List<DeviceDTO> devices) {
        super(CommandType.SendDeviceList);
        this.gateId = gateId;
        this.devices = devices;
    }

    public String getGateId() {
        return gateId;
    }

    public List<DeviceDTO> getDevices() {
        return devices;
    }
}
