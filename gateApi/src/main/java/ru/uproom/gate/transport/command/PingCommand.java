package ru.uproom.gate.transport.command;

/**
 * Command for ping gate from server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class PingCommand extends Command {

    public PingCommand() {
        super(CommandType.Ping);
    }

}
