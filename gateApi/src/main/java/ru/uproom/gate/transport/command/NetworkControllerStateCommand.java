package ru.uproom.gate.transport.command;

import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * User: osipenko
 * Date: 14/09/17
 * Time: 16:10
 */
public class NetworkControllerStateCommand extends Command {
    private static final long serialVersionUID = -6578947392045447679L;
    private DeviceStateEnum state;

    public NetworkControllerStateCommand(DeviceStateEnum state) {
        super(CommandType.NetworkControllerState);
        this.state = state;
    }

    public DeviceStateEnum getState() {
        return state;
    }
}
