package ru.uproom.gate.localinterface.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.localinterface.output.GateLocalOutput;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.SendDeviceListCommand;

/**
 * Command for get device list from server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
@GateLocalCommandHandlerAnnotation(value = CommandType.SendDeviceList)
public class SendDeviceListGateLocalCommandHandler implements GateLocalCommandHandler {

    private static final Logger LOG =
            LoggerFactory.getLogger(SendDeviceListGateLocalCommandHandler.class);

    @Override
    public boolean execute(Command command, GateLocalOutput output) {

        if (command == null || !(command instanceof SendDeviceListCommand)) return false;

        SendDeviceListCommand cmd = (SendDeviceListCommand) command;
        output.setListDTO(cmd.getDevices());

        LOG.debug("have command from gate : {}", command.getType());

        return true;
    }

}
