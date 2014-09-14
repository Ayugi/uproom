package ru.uproom.gate.notifications;

import ru.uproom.gate.transport.dto.parameters.DeviceState;

/**
 * Handler for Z-Wave notification DRIVER_READY
 * <p/>
 * Created by osipenko on 11.09.14.
 */
public class DeviceReadyNotificationHandler extends DeviceNotificationHandler {
    @Override
    protected DeviceState getEvent() {
        return DeviceState.Ready;
    }
}
