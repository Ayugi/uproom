package ru.uproom.gate.notifications;

import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandler(value = NotificationType.ESSENTIAL_NODE_QUERIES_COMPLETE)
public class EssentialNodeQueriesCompleteNotificationHandler implements NotificationHandler {

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {
        return false;
    }
}
