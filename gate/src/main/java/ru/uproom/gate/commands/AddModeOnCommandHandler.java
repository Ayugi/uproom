package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.AddModeOnCommand;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Handler for command which set controller in add device mode
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.AddModeOn)
public class AddModeOnCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AddModeOnCommandHandler.class);

    @Override
    public boolean execute(Command command, GateDevicesSet home) {

        if (command == null || !(command instanceof AddModeOnCommand)) return false;
        home.requestMode(DeviceStateEnum.Add);

        LOG.debug("Receive command {}", command.getType());

        return true;
    }

    @Override
    public void stop() {

    }

}
