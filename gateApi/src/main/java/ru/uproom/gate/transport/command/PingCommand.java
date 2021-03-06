package ru.uproom.gate.transport.command;

/**
 * Command for ping gate from server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class PingCommand extends Command {
    private static final long serialVersionUID = -8252608478060876864L;
    private long issued;
    private int linkId;

    public PingCommand() {
        super(CommandType.Ping);
        issued = System.currentTimeMillis();
    }

    public long getIssued() {
        return issued;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }
}
