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

@ZwaveNotificationHandler(value = NotificationType.NODE_REMOVED)
public class NodeRemovedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NodeRemovedNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // Определение идентификатора узла и удаление его из списка известных узлов
        short nodeId = notification.getNodeId();
        home.getNodes().remove(nodeId);

        LOG.debug("z-wave notification : NODE_REMOVED; node ID : {}", nodeId);
        return false;
    }
}
