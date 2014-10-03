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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.ALL_NODES_QUERIED_SOME_DEAD)
public class AllNodesQueriedSomeDeadNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AllNodesQueriedSomeDeadNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        if (home == null || notification == null) return false;

        Manager.get().writeConfig(home.getHomeId());
        home.setReady(true);
        home.setControllerState(DeviceStateEnum.Work, false);

        LOG.debug("z-wave notification : {}; z-wave state : {}",
                new Object[]{notification.getType(), home.getControllerState()});

        return true;
    }
}
