package ru.uproom.gate.transport;

/**
 * Created with IntelliJ IDEA.
 * User: zhulant
 * Date: 8/29/14
 * Time: 12:48 PM
 */
public class HandshakeCommand extends Command{
    private String gateId;
    public HandshakeCommand(String gateId) {
        super(CommandType.Handshake);
        this.gateId = gateId;
    }

    public String getGateId() {
        return gateId;
    }
}
