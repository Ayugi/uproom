package ru.uproom.gate.transport.command;

/**
 * Command for create device list on the gate and send it to server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class GetDeviceListCommand extends Command {

    private static final long serialVersionUID = -3301266621328124248L;
    private int gateId;

    public GetDeviceListCommand() {
        super(CommandType.GetDeviceList);
    }

    public GetDeviceListCommand(int gateId) {
        super(CommandType.GetDeviceList);
        this.gateId = gateId;
    }

    public int getGateId() {
        return gateId;
    }
}
