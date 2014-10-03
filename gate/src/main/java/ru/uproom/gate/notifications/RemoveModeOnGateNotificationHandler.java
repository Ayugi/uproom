package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import ru.uproom.gate.devices.GateDevicesSet;

/**
 * Created by osipenko on 15.09.14.
 */

@GateNotificationHandlerAnnotation(value = GateNotificationType.RemoveModeOn)
public class RemoveModeOnGateNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RemoveModeOnGateNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        if (home == null) return false;

        LOG.debug("Z-Wave controller set Remove Mode");

        // send message to server
        //transport.sendCommand(new NetworkControllerStateCommand(home.getControllerState()));
        return true;
    }
}
