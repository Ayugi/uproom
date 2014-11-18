package ru.uproom.gate.transport.command;

/**
 * Command for set Remove Mode on Z-Wave Controller
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class CancelCommand extends Command {

    private static final long serialVersionUID = 6835831035960920159L;

    public CancelCommand() {
        super(CommandType.Cancel);
    }

}
