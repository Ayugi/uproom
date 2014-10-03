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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.NOTIFICATION)
public class NotificationNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        LOG.debug("z-wave notification : {}; node : {}; type : {}", new Object[]{
                notification.getType(),
                notification.getNodeId(),
                NodeNotificationType.byCode((int) notification.getNotification())
        });

        return true;
    }
}
