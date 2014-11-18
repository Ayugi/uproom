package ru.uproom.gate.transport.command;

/**
 * Command for set Remove Mode on Z-Wave Controller
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class RemoveModeOnCommand extends Command {

    private static final long serialVersionUID = 3908438994837907536L;

    public RemoveModeOnCommand() {
        super(CommandType.RemoveModeOn);
    }

}
