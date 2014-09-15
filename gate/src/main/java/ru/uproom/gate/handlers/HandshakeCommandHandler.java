package ru.uproom.gate.handlers;

import ru.uproom.gate.notifications.GateWatcher;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.HandshakeCommand;

/**
 * Handler for command Handshake
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandAnnotation(value = CommandType.Handshake)
public class HandshakeCommandHandler implements CommandHandler {

    @Override
    public boolean execute(Command command, GateWatcher watcher) {

        if (command == null || !(command instanceof HandshakeCommand)) return false;

        System.out.println("HandshakeCommandHandler >>>> handshake gateId = " + ((HandshakeCommand) command).getGateId());

        return true;
    }

}
