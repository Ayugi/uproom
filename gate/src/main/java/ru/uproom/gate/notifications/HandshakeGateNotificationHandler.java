package ru.uproom.gate.notifications;

import org.zwave4j.Notification;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.HandshakeCommand;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Handler which create device list and send it to server
 * <p/>
 * Created by osipenko on 10.09.14.
 */
@GateNotificationHandlerAnnotation(value = GateNotificationType.Handshake)
public class HandshakeGateNotificationHandler implements NotificationHandler {
    @Override
    public boolean execute(int gateId, ZWaveHome home, ServerTransportMarker transport, Notification notification) {
        if (transport == null) return false;

        return transport.sendCommand(new HandshakeCommand(
                (int) home.getHomeId()
        ));

    }
}
