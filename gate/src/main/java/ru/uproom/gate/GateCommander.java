package ru.uproom.gate;

import ru.uproom.gate.transport.Command;

/**
 * Marker interface for command handlers pool
 * <p/>
 * Created by osipenko on 06.09.14.
 */
public interface GateCommander {
    public boolean execute(Command command);
}