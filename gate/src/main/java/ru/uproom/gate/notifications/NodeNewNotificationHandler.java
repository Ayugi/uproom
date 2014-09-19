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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.NODE_NEW)
public class NodeNewNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NodeNewNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // Определение идентификатора узла и добавление его в список известных узлов
        ZWaveNode node = new ZWaveNode(home, notification.getNodeId());
        home.getNodes().put(node.getZId(), node);

        LOG.debug("z-wave notification : NODE_NEW; node ID : {}", node.getZId());
        return false;
    }
}
