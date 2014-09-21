package ru.uproom.gate.notifications;

import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Marker interface for object handling inline gate notifications
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public interface GateWatcher {
    public ZWaveHome getHome();

    public boolean onGateEvent(GateNotificationType type, DeviceDTO device);
}
