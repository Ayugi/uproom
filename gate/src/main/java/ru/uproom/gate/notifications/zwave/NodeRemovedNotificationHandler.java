package ru.uproom.gate.notifications.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.notifications.NotificationHandler;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.NODE_REMOVED)
public class NodeRemovedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NodeRemovedNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        home.removeGateDevice((int) notification.getNodeId());

        LOG.debug("z-wave notification : {}; node ID : {}",
                new Object[]{notification.getType(), notification.getNodeId()});

        return true;
    }
}
