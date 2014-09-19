package ru.uproom.gate.notifications;

import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.dto.parameters.DeviceState;

/**
 * Handler for Z-Wave notification DRIVER_READY
 * <p/>
 * Created by osipenko on 11.09.14.
 */
@ZwaveNotificationHandler(value = NotificationType.DRIVER_FAILED)
public class DeviceFailedNotificationHandler extends DeviceNotificationHandler {

    @Override
    protected DeviceState getEvent() {
        return DeviceState.Failed;
    }

}
