package ru.uproom.gate.notifications.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.notifications.NotificationHandler;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.DRIVER_FAILED)
public class DriverFailedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFailedNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        if (home == null) return false;

        home.setHomeId(0);
        home.setReady(false);
        home.setFailed(true);
        home.setControllerState(DeviceStateEnum.Down, false);

        LOG.debug("z-wave notification : {}", notification.getType());

        return true;
    }

}