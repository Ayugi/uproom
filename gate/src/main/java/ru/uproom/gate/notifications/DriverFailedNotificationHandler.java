package ru.uproom.gate.notifications;

import org.zwave4j.Manager;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.DeviceState;
import ru.uproom.gate.transport.dto.DeviceType;
import ru.uproom.gate.zwave.ZWaveHome;
import ru.uproom.gate.zwave.ZWaveNode;

/**
 * Handler for Z-Wave notification DRIVER_READY
 * <p/>
 * Created by osipenko on 11.09.14.
 */
public class DriverFailedNotificationHandler implements NotificationHandler {
    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport) {

        // find controller node
        short controllerId = Manager.get().getControllerNodeId(home.getHomeId());
        ZWaveNode controller = home.get(controllerId);
        // set controller state
        if (controller != null) controller.setState(DeviceState.Failed);
        // create message about this
        Command command = new SetDeviceParameterCommand();
        if (controller != null)
            command.getDevices().add(controller.getDeviceInfo());
        else {
            DeviceDTO device = new DeviceDTO(0, home.getHomeId(), (short) 0, DeviceType.Controller);
            device.getParameters().put("NodeState", DeviceState.Failed.name());
            command.getDevices().add(device);
        }

        // send message to server
        return transport.sendCommand(command);
    }
}
