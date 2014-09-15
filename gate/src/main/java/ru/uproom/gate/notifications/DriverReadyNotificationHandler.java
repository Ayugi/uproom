package ru.uproom.gate.notifications;

import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandler(value = NotificationType.DRIVER_READY)
public class DriverReadyNotificationHandler implements NotificationHandler {

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        if (home == null || notification == null) return false;
        // HomeID in Z-Wave notation - our GateID
        home.setHomeId(notification.getHomeId());

        return true;
    }
}
