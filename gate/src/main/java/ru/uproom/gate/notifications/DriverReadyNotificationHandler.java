package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.DRIVER_READY)
public class DriverReadyNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DriverReadyNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        if (home == null || notification == null) return false;
        // HomeID in Z-Wave notation - our GateID
        home.setHomeId(notification.getHomeId());

        LOG.debug("z-wave notification : DRIVER_READY,  Home ID : {}", home.getHomeId());

        return true;
    }
}
