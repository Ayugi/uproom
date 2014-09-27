package ru.uproom.gate.notifications;

import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

/**
 * Handler for Z-Wave notification DRIVER_READY
 * <p/>
 * Created by osipenko on 11.09.14.
 */
public class DeviceReadyNotificationHandler extends DeviceNotificationHandler {
    @Override
    protected DeviceStateEnum getEvent() {
        return DeviceStateEnum.Work;
    }
}
