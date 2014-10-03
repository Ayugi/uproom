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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.DELETE_BUTTON)
public class DeleteButtonNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteButtonNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        LOG.debug("z-wave notification : {}", notification.getType());

        return false;
    }
}
