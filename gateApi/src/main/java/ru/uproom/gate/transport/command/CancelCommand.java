package ru.uproom.gate.transport.command;

/**
 * Command for set Remove Mode on Z-Wave Controller
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class CancelCommand extends Command {

    public CancelCommand() {
        super(CommandType.Cancel);
    }

}
