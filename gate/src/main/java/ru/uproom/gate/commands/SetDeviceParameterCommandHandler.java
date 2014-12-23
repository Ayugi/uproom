package ru.uproom.gate.commands;

import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;

/**
 * Command for set any device parameter
 * <p/>
 * Created by osipenko on 10.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.SetDeviceParameter)
public class SetDeviceParameterCommandHandler implements CommandHandler {

    @Override
    public boolean execute(Command command, GateDevicesSet home) {
        if (!(command instanceof SetDeviceParameterCommand)) return false;

        DeviceDTO dto = ((SetDeviceParameterCommand) command).getDevice();
        if (dto == null) return false;
        home.setDeviceDTO(dto);

        return true;
    }

    @Override
    public void stop() {

    }
}
