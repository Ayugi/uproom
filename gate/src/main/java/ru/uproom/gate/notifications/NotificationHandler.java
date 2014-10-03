package ru.uproom.gate.notifications;

import org.zwave4j.Notification;
import ru.uproom.gate.devices.GateDevicesSet;

/**
 * marker interface for classes of notifications handling
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public interface NotificationHandler {
    public boolean execute(Notification notification, GateDevicesSet home);
}
