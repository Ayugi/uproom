package ru.uproom.gate.transport.command;

/**
 * User: osipenko
 * Date: 14/09/17
 * Time: 16:10
 */
public class NetworkControllerStateCommand extends Command {
    private String gateId;
    private String state;

    public NetworkControllerStateCommand(String gateId, String state) {
        super(CommandType.NetworkControllerState);
        this.gateId = gateId;
        this.state = state;
    }

    public String getGateId() {
        return gateId;
    }

    public String getState() {
        return state;
    }
}
