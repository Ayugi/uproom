package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.RemoveModeOnCommand;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Handler for command which set controller in remove device mode
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.RemoveModeOn)
public class RemoveModeOnCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RemoveModeOnCommandHandler.class);

    @Override
    public boolean execute(Command command, GateDevicesSet home) {

        if (command == null || !(command instanceof RemoveModeOnCommand)) return false;
        home.requestMode(DeviceStateEnum.Remove);

        LOG.debug("Receive command Remove Mode On");

        return true;
    }

}
