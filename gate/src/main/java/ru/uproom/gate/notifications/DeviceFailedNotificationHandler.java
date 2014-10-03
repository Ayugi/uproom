package ru.uproom.gate.notifications;

import org.zwave4j.NotificationType;
import ru.uproom.gate.notifications.zwave.ZwaveNotificationHandlerAnnotation;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Handler for Z-Wave notification DRIVER_READY
 * <p/>
 * Created by osipenko on 11.09.14.
 */
@ZwaveNotificationHandlerAnnotation(value = NotificationType.DRIVER_FAILED)
public class DeviceFailedNotificationHandler extends DeviceNotificationHandler {

    @Override
    protected DeviceStateEnum getEvent() {
        return DeviceStateEnum.Down;
    }

}
