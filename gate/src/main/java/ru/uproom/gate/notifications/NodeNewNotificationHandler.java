package ru.uproom.gate.notifications;

import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandler(value = NotificationType.NODE_NEW)
public class NodeNewNotificationHandler implements NotificationHandler {

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {
        return false;
    }
}
