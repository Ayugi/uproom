package ru.uproom.gate.notifications.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Manager;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.devices.zwave.ZWaveValueIndexFactory;
import ru.uproom.gate.notifications.NotificationHandler;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandlerAnnotation(value = NotificationType.VALUE_REFRESHED)
public class ValueRefreshedNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ValueRefreshedNotificationHandler.class);

    @Override
    public boolean execute(Notification notification, GateDevicesSet home) {

        int paramIndex = ZWaveValueIndexFactory.createIndex(notification.getValueId());
        DeviceParametersNames paramName = DeviceParametersNames.byZWaveCode(paramIndex);

        home.setGateDeviceParameter(notification.getNodeId(), paramName, notification.getValueId());

        LOG.debug("z-wave notification : {}; node : {}; label : {}", new Object[]{
                notification.getType(),
                notification.getNodeId(),
                Manager.get().getValueLabel(notification.getValueId()),
        });

        return true;
    }
}
