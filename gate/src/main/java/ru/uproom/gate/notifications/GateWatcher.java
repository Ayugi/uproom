package ru.uproom.gate.notifications;

import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.dto.DeviceDTO;

/**
 * Marker interface for object handling inline gate notifications
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public interface GateWatcher {

    public GateDevicesSet getHome();

    public void setHome(GateDevicesSet home);

    public boolean onGateEvent(GateNotificationType type, DeviceDTO device);
}
