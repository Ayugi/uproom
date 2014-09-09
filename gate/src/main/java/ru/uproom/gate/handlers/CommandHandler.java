package ru.uproom.gate.handlers;

import ru.uproom.gate.transport.Command;

/**
 * marker interface for classes of command handling
 * </p>
 * Created by osipenko on 30.08.14.
 */
public interface CommandHandler {
    public boolean execute(Command command);
}
