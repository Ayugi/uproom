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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.NODE_QUERIES_COMPLETE)
public class NodeQueriesCompleteNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NodeQueriesCompleteNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // call listeners for this node
        ZWaveNode node = home.getNodes().get(notification.getNodeId());
        if (node == null) return false;
        node.callEvents(notification);

        LOG.debug("z-wave notification : NODE_QUERIES_COMPLETE; node ID : {}", node.getZId());
        return true;
    }
}
