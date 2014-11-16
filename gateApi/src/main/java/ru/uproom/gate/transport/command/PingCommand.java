package ru.uproom.gate.transport.command;

/**
 * Command for ping gate from server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class PingCommand extends Command {
    private long issued;

    public PingCommand() {
        super(CommandType.Ping);
        issued = System.currentTimeMillis();
    }

    public long getIssued() {
        return issued;
    }
}
