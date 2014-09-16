package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.zwave.ZWaveHome;
import ru.uproom.gate.zwave.ZWaveNode;
import ru.uproom.gate.zwave.ZWaveValue;
import ru.uproom.gate.zwave.ZWaveValueIndexFactory;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandler(value = NotificationType.VALUE_CHANGED)
public class ValueChangedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ValueChangedNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // find node
        ZWaveNode node = home.getNodes().get(notification.getNodeId());
        if (node == null) return false;

        // find value
        Integer index = ZWaveValueIndexFactory.createIndex(
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex()
        );
        ZWaveValue value = node.getValues().get(index);
        if (value == null) return false;

        // call listeners
        value.callEvents();

        LOG.debug("z-wave notification : VALUE_CHANGED; node ID : {}; value label : {}",
                node.getZId(),
                value.getValueLabel()
        );
        return true;
    }
}
