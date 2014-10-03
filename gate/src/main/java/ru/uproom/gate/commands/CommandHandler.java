package ru.uproom.gate.commands;

import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.Command;

/**
 * marker interface for classes of command handling
 * </p>
 * Created by osipenko on 30.08.14.
 */
public interface CommandHandler {
    public boolean execute(Command command, GateDevicesSet home);
}
