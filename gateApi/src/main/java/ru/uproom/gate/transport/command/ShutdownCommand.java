package ru.uproom.gate.transport.command;

/**
 * Command for shutdown gate
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public class ShutdownCommand extends Command {
    private int gateId;

    public ShutdownCommand(int gateId) {
        super(CommandType.Shutdown);
        this.gateId = gateId;
    }

    public int getGateId() {
        return gateId;
    }
}
