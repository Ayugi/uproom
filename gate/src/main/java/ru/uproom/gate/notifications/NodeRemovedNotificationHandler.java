package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.zwave.ZWaveHome;
import ru.uproom.gate.zwave.ZWaveNode;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.NODE_REMOVED)
public class NodeRemovedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NodeRemovedNotificationHandler.class);

    @Override
    public boolean execute(int gateId, ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // Определение идентификатора узла и удаление его из списка известных узлов
        short nodeId = notification.getNodeId();
        ZWaveNode node = home.getNodes().remove(nodeId);

        LOG.debug("z-wave notification : NODE_REMOVED; node ID : {}", nodeId);

        // send information about node to server
        if (!home.isReady()) return false;
        node.setZId((short) 0);
        return transport.sendCommand(new SetDeviceParameterCommand(node.getDeviceInfo()));
    }
}
