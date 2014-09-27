package ru.uproom.gate.transport.command;

import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * User: osipenko
 * Date: 14/09/17
 * Time: 16:10
 */
public class NetworkControllerStateCommand extends Command {
    private int gateId;
    private DeviceStateEnum state;

    public NetworkControllerStateCommand(int gateId, DeviceStateEnum state) {
        super(CommandType.NetworkControllerState);
        this.gateId = gateId;
        this.state = state;
    }

    public int getGateId() {
        return gateId;
    }

    public DeviceStateEnum getState() {
        return state;
    }
}
