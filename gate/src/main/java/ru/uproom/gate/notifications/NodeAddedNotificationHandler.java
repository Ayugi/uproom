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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.NODE_ADDED)
public class NodeAddedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NodeAddedNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // add node in gate node list
        ZWaveNode node = new ZWaveNode(home, notification.getNodeId());
        home.getNodes().put(node.getZId(), node);

        LOG.debug("z-wave notification : NODE_ADDED; node ID : {}", node.getZId());

        // send information about node to server
        if (!home.isReady()) return false;
        return transport.sendCommand(new SetDeviceParameterCommand(node.getDeviceInfo()));
    }
}
