package ru.uproom.gate.commands;

import ru.uproom.gate.transport.command.Command;

/**
 * Marker interface for command commands pool
 * <p/>
 * Created by osipenko on 06.09.14.
 */
public interface GateCommander {
    public boolean execute(Command command);
}