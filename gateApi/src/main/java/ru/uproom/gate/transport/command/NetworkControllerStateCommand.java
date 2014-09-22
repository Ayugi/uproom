package ru.uproom.gate.transport.command;

/**
 * User: osipenko
 * Date: 14/09/17
 * Time: 16:10
 */
public class NetworkControllerStateCommand extends Command {
    private int gateId;
    private String state;

    public NetworkControllerStateCommand(int gateId, String state) {
        super(CommandType.NetworkControllerState);
        this.gateId = gateId;
        this.state = state;
    }

    public int getGateId() {
        return gateId;
    }

    public String getState() {
        return state;
    }
}
