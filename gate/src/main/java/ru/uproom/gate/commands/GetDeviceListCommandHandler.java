package ru.uproom.gate.commands;

import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;

/**
 * Command for create device list and send it to server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.GetDeviceList)
public class GetDeviceListCommandHandler implements CommandHandler {

    @Override
    public boolean execute(Command command, GateDevicesSet home) {
        home.getDeviceDTOList();
        return true;
    }

    @Override
    public void stop() {

    }
}
