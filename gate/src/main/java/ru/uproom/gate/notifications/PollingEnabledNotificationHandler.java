package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 15.09.14.
 */

@ZwaveNotificationHandler(value = NotificationType.POLLING_ENABLED)
public class PollingEnabledNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PollingEnabledNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        LOG.debug("z-wave notification : POLLING_ENABLED");
        return false;
    }
}
