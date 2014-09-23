package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;
import ru.uproom.gate.zwave.ZWaveHome;
import ru.uproom.gate.zwave.ZWaveNode;
import ru.uproom.gate.zwave.ZWaveValue;
import ru.uproom.gate.zwave.ZWaveValueIndexFactory;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.VALUE_CHANGED)
public class ValueChangedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ValueChangedNotificationHandler.class);

    @Override
    public boolean execute(int gateId, ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        // find node
        ZWaveNode node = home.getNodes().get(notification.getNodeId());
        if (node == null) return false;

        // find value
        Integer index = ZWaveValueIndexFactory.createIndex(notification.getValueId());
        DeviceParametersNames name = DeviceParametersNames.byZWaveCode(index);
        ZWaveValue value = (ZWaveValue) node.getParams().get(name);
        if (value == null) return false;

        // call listeners
        value.callEvents();

        System.out.println(String.format("VALUE CHANGED : node=%d, label='%s', id=%d",
                node.getZId(),
                value.getValueLabel(),
                value.getId()
        ));
        LOG.debug("z-wave notification : VALUE_CHANGED; node ID : {}; value label : {}",
                node.getZId(),
                value.getValueLabel()
        );

        // send message
        return transport.sendCommand(new SetDeviceParameterCommand(
                node.getDeviceParameters(new DeviceParametersNames[]{value.getValueName()})
        ));
    }
}
