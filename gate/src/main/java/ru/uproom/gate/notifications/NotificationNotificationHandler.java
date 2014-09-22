package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.zwave.ZWaveHome;
import ru.uproom.gate.zwave.ZWaveNode;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.NOTIFICATION)
public class NotificationNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationNotificationHandler.class);

    @Override
    public boolean execute(int gateId, ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        String notificationCode = "";

        // find node, if not exist - create it
        ZWaveNode node = home.getNodes().get(notification.getNodeId());
        if (node == null) node = new ZWaveNode(home, notification.getNodeId());

        // call listeners
        node.callEvents(notification);

        // additional notification codes
        switch (notification.getNotification()) {

            case 0: // MSG_COMPLETE
                notificationCode = "MSG_COMPLETE";
                break;

            case 1: // TIMEOUT
                notificationCode = "TIMEOUT";
                break;

            case 2: // NO_OPERATION
                notificationCode = "NO_OPERATION";
                break;

            case 3: // AWAKE
                notificationCode = "AWAKE";
                break;

            case 4: // SLEEP
                notificationCode = "SLEEP";
                break;

            case 5: // DEAD
                notificationCode = "DEAD";
                home.getNodes().remove(node.getZId());
                break;

            case 6: // ALIVE
                notificationCode = "ALIVE";
                home.getNodes().put(notification.getNodeId(), node);
                break;

            default:
                notificationCode = String.format("UNKNOWN (%d)", notification.getNotification());

        }

        LOG.debug("z-wave notification : NOTIFICATION; code : {}", notificationCode, node.getZId());
        return true;
    }
}
