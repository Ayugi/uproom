package ru.uproom.gate.notifications;

import org.zwave4j.Manager;
import org.zwave4j.Notification;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.devices.zwave.ZWaveNode;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.DeviceType;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Created by HEDIN on 12.09.2014.
 */
public abstract class DeviceNotificationHandler implements NotificationHandler {

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        // find controller node
        short controllerId = Manager.get().getControllerNodeId(home.getHomeId());
        ZWaveNode controller = null;//home.getNodes().get(controllerId);
        // set controller state
        //if (controller != null) controller.setState(getEvent());
        // create message about this

        // send message to server
        return true;
//        return transport.sendCommand(
//                prepareCommand(home, controller));
    }

    protected abstract DeviceStateEnum getEvent();

    private SetDeviceParameterCommand prepareCommand(GateDevicesSet home, ZWaveNode controller) {
        if (controller != null)
            return new SetDeviceParameterCommand(controller.getDeviceDTO());
        else {
            DeviceDTO device = new DeviceDTO(0, (short) 0, DeviceType.Controller);
            //device.getParameters().put("NodeState", getEvent().name());
            return new SetDeviceParameterCommand(device);
        }
    }
}
