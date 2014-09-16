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

@ZwaveNotificationHandler(value = NotificationType.GROUP)
public class GroupNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GroupNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // node associated with groups
        ZWaveNode node = home.getNodes().get(notification.getNodeId());
        if (node == null) return false;

        // add group if not exist
        if (!node.existGroup(notification.getGroupIdx()))
            node.getGroups().add(notification.getGroupIdx());

        LOG.debug("z-wave notification : GROUP; node ID : {}; group ID : {}",
                node.getZId(),
                notification.getGroupIdx()
        );
        return true;
    }
}
