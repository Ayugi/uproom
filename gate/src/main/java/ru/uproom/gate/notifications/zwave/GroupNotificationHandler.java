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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.GROUP)
public class GroupNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GroupNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        home.addDeviceGroup((int) notification.getNodeId(), (int) notification.getGroupIdx());

        LOG.debug("z-wave notification : {}; node ID : {}; group ID : {}",
                new Object[]{notification.getType(), notification.getNodeId(), notification.getGroupIdx()});

        return true;
    }
}
