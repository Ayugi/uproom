package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.zwave.ZWaveHome;
import ru.uproom.gate.zwave.ZWaveNode;
import ru.uproom.gate.zwave.ZWaveValue;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.VALUE_ADDED)
public class ValueAddedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ValueAddedNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // find node in home
        ZWaveNode node = home.getNodes().get(notification.getNodeId());
        if (node == null) return false;

        // add new value to finding node
        ZWaveValue value = new ZWaveValue(notification.getValueId());
        node.getParams().put(value.getValueName(), value);

        System.out.println(String.format("VALUE ADDED : node=%d, label='%s', id=%d",
                node.getZId(),
                value.getValueLabel(),
                value.getId()
        ));
        LOG.debug("z-wave notification : VALUE_ADDED; node ID : {}; value label : {}",
                node.getZId(),
                value.getValueLabel()
        );

        // send information about node to server
        if (!home.isReady()) return false;
        return transport.sendCommand(new SetDeviceParameterCommand(node.getDeviceInfo()));
    }
}
