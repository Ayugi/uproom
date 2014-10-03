package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import ru.uproom.gate.devices.GateDevicesSet;

/**
 * Created by osipenko on 15.09.14.
 */

@GateNotificationHandlerAnnotation(value = GateNotificationType.Cancel)
public class CancelGateNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CancelGateNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        if (home == null) return false;

        LOG.debug("Z-Wave controller Cancel current mode");

        // send message to server
        //transport.sendCommand(new NetworkControllerStateCommand(home.getControllerState()));
        return true;
    }
}
