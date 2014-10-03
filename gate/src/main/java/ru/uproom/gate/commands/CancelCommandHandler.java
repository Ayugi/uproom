package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.CancelCommand;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Handler for command which cancel current mode of Z-Wave controller
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.Cancel)
public class CancelCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CancelCommandHandler.class);

    @Override
    public boolean execute(Command command, GateDevicesSet home) {

        if (command == null || !(command instanceof CancelCommand)) return false;
        home.requestMode(DeviceStateEnum.Cancel);

        LOG.debug("Receive command Cancel");

        return true;
    }

}
