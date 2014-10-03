package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.HandshakeCommand;

/**
 * Handler for command Handshake
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.Handshake)
public class HandshakeCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HandshakeCommandHandler.class);

    @Override
    public boolean execute(Command command, GateDevicesSet home) {

        if (command == null || !(command instanceof HandshakeCommand)) return false;

        LOG.debug("Handshake from gate with ID = {}", ((HandshakeCommand) command).getGateId());

        return true;
    }

}
