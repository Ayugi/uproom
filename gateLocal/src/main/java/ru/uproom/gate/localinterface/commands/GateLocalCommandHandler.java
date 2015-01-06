package ru.uproom.gate.localinterface.commands;

import ru.uproom.gate.localinterface.output.GateLocalOutput;
import ru.uproom.gate.transport.command.Command;

/**
 * marker interface for classes of command handling
 * <p/>
 * Created by osipenko on 30.08.14.
 */
public interface GateLocalCommandHandler {

    public boolean execute(Command command, GateLocalOutput output);

}
