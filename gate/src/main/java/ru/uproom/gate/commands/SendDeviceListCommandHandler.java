package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.SendDeviceListCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;

/**
 * Command for get device list from server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.SendDeviceList)
public class SendDeviceListCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SendDeviceListCommandHandler.class);

    @Override
    public boolean execute(Command command, GateDevicesSet home) {

        if (command == null || !(command instanceof SendDeviceListCommand)) return false;

        for (DeviceDTO dto : ((SendDeviceListCommand) command).getDevices()) {
            home.setDeviceDTO(dto);
        }

        LOG.debug("Handle server command : {}", command.getType());

        return true;
    }
}
