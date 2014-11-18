package ru.uproom.gate.transport.command;

/**
 * Command for set Add Mode on Z-Wave Controller
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class AddModeOnCommand extends Command {

    private static final long serialVersionUID = 6648138578201360144L;

    public AddModeOnCommand() {
        super(CommandType.AddModeOn);
    }

}
