package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;

/**
 * Handler for command which set controller in add device mode
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.Ping)
public class PingCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PingCommandHandler.class);

    @Override
    public boolean execute(Command command, GateDevicesSet home) {

        home.ping();

        LOG.debug("Receive command {}", command.getType());

        return true;
    }

}
