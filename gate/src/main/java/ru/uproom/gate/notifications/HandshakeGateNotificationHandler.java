package ru.uproom.gate.notifications;

import org.zwave4j.Notification;
import ru.uproom.gate.devices.GateDevicesSet;

/**
 * Handler which create device list and send it to server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
@GateNotificationHandlerAnnotation(value = GateNotificationType.Handshake)
public class HandshakeGateNotificationHandler implements NotificationHandler {
    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {
        //if (transport == null) return false;

        return true;//transport.sendCommand(new HandshakeCommand(
//                (int) home.getHomeId()
//        ));

    }
}
