package ru.uproom.gate.handlers;

import ru.uproom.gate.transport.Command;
import ru.uproom.gate.transport.HandshakeCommand;

/**
 * Handler for command Handshake
 * </p>
 * Created by osipenko on 09.09.14.
 */
public class HandshakeCommandHandler implements CommandHandler {

    @Override
    public boolean execute(Command command) {

        if (command == null || !(command instanceof HandshakeCommand)) return false;

        System.out.println("HandshakeCommandHandler >>>> handshake gateId = " + ((HandshakeCommand) command).getGateId());

        return true;
    }

}
