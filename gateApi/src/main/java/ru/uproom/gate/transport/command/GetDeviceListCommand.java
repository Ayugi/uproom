package ru.uproom.gate.transport.command;

/**
 * Command for create device list on the gate and send it to server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class GetDeviceListCommand extends Command {
    private String gateId;

    public GetDeviceListCommand(String gateId) {
        super(CommandType.GetDeviceList);
        this.gateId = gateId;
    }

    public String getGateId() {
        return gateId;
    }
}
