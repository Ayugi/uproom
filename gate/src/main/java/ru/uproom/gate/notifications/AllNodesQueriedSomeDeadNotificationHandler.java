package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Manager;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandler(value = NotificationType.ALL_NODES_QUERIED_SOME_DEAD)
public class AllNodesQueriedSomeDeadNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AllNodesQueriedSomeDeadNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        if (home == null || notification == null) return false;
        // Z-Wave Network ready
        Manager.get().writeConfig(home.getHomeId());
        home.setReady(true);
        // todo : send message to server

        LOG.debug("z-wave notification : ALL_NODES_QUERIED_SOME_DEAD, z-wave network : READY");
        return false;
    }
}
