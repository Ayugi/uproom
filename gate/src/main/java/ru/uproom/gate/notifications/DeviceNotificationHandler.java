package ru.uproom.gate.notifications;

import org.zwave4j.Manager;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.DeviceType;
import ru.uproom.gate.transport.dto.parameters.DeviceState;
import ru.uproom.gate.zwave.ZWaveHome;
import ru.uproom.gate.zwave.ZWaveNode;

/**
 * Created by HEDIN on 12.09.2014.
 */
public abstract class DeviceNotificationHandler implements NotificationHandler {

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport) {

        // find controller node
        short controllerId = Manager.get().getControllerNodeId(home.getHomeId());
        ZWaveNode controller = home.get(controllerId);
        // set controller state
        if (controller != null) controller.setState(getEvent());
        // create message about this

        // send message to server
        return transport.sendCommand(
                prepareCommand(home, controller));
    }

    protected abstract DeviceState getEvent();

    private SetDeviceParameterCommand prepareCommand(ZWaveHome home, ZWaveNode controller) {
        if (controller != null)
            return new SetDeviceParameterCommand(controller.getDeviceInfo());
        else {
            DeviceDTO device = new DeviceDTO(0, home.getHomeId(), (short) 0, DeviceType.Controller);
            device.getParameters().put("NodeState", getEvent().name());
            return new SetDeviceParameterCommand(device);
        }
    }
}
