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

@ZwaveNotificationHandler(value = NotificationType.VALUE_ADDED)
public class ValueAddedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ValueAddedNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // находим узел в который добавляется параметр
        ZWaveNode node = home.getNodes().get(notification.getNodeId());
        if (node == null) return false;

        // добавляем параметр
        ZWaveValue value = new ZWaveValue(notification.getValueId());
        Integer index = ZWaveValueIndexFactory.createIndex(
                value.getValueCommandClass(),
                value.getValueInstance(),
                value.getValueIndex()
        );
        node.getValues().put(index, value);

        LOG.debug("z-wave notification : VALUE_ADDED; node ID : {}; value label : {}",
                node.getZId(),
                value.getValueLabel()
        );
        return true;
    }
}
