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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.DRIVER_READY)
public class DriverReadyNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DriverReadyNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        if (home == null || notification == null) return false;

        home.setHomeId(notification.getHomeId());

        LOG.debug("z-wave notification : {},  Home ID : {}",
                new Object[]{notification.getType(), home.getHomeId()});

        return true;
    }
}
