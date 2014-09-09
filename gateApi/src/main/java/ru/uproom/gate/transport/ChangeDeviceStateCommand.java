package ru.uproom.gate.transport;

/**
 * User: Tiberr
 * Date: 09/09/14
 * Time: 10:57 AM
 */
public class ChangeDeviceStateCommand extends Command {
    private String gateId;
    private short nodeId;
    private String state;

    public ChangeDeviceStateCommand(String gateId, short nodeId, String state) {
        super(CommandType.ChangeDeviceState);
        this.gateId = gateId;
        this.nodeId = nodeId;
        this.state = state;
    }

    public String getGateId() {
        return gateId;
    }

    public short getNodeId() {
        return nodeId;
    }

    public String getState() {
        return state;
    }
}
