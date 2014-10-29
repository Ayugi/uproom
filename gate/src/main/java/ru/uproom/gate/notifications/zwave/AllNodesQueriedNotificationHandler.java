package ru.uproom.gate.notifications.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Manager;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.notifications.NotificationHandler;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.ALL_NODES_QUERIED)
public class AllNodesQueriedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AllNodesQueriedNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        if (home == null || notification == null) return false;
        // Z-Wave Network ready
        Manager.get().writeConfig(home.getHomeId());
        home.setReady(true);
        home.setControllerState(DeviceStateEnum.Work, false);

        LOG.debug("z-wave notification : {}, z-wave network : {}",
                new Object[]{notification.getType(), home.getControllerState()});

        // send message to server
        return true;
    }
}
