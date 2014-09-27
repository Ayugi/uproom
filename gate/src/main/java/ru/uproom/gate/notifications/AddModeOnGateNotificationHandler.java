package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.command.NetworkControllerStateCommand;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 15.09.14.
 */

@GateNotificationHandlerAnnotation(value = GateNotificationType.AddModeOn)
public class AddModeOnGateNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AddModeOnGateNotificationHandler.class);

    @Override
    public boolean execute(int gateId, ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        if (home == null || transport == null) return false;

        LOG.debug("Z-Wave controller set Add Mode");

        // send message to server
        return transport.sendCommand(new NetworkControllerStateCommand(gateId, home.getControllerState()));
    }
}
