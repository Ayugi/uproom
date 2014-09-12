package ru.uproom.gate.notifications;

/**
 * Marker interface for object handling inline gate notifications
 * <p/>
 * Created by osipenko on 10.09.14.
 */
public interface GateWatcher {
    public boolean onGateEvent(GateNotificationType type);
}
