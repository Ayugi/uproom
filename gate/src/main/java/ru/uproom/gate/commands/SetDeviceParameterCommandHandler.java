package ru.uproom.gate.commands;

import ru.uproom.gate.notifications.GateWatcher;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.zwave.ZWaveNode;

/**
 * Command for set any device parameter
 * <p/>
 * Created by osipenko on 10.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.SetDeviceParameter)
public class SetDeviceParameterCommandHandler implements CommandHandler {

    @Override
    public boolean execute(Command command, GateWatcher watcher) {
        if (!(command instanceof SetDeviceParameterCommand)) return false;

        DeviceDTO dto = ((SetDeviceParameterCommand) command).getDevice();
        if (dto == null) return false;
        ZWaveNode node = watcher.getHome().getNodes().get(dto.getZId());
        if (node == null) return false;

        return node.setParams(dto);
    }
}
