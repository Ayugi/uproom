package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.NetworkControllerStateCommand;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.DRIVER_FAILED)
public class DriverFailedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFailedNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        if (home == null || notification == null) return false;
        // reset HomeID
        home.setHomeId(0);
        home.setReady(false);

        LOG.debug("z-wave notification : DRIVER_FAILED");

        // send message to server
        return transport.sendCommand(new NetworkControllerStateCommand(home.getHomeIdAsString(), "off"));

    }

}
