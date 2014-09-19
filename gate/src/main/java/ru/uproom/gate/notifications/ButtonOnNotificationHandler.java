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

@ZwaveNotificationHandlerAnnotation(value = NotificationType.BUTTON_ON)
public class ButtonOnNotificationHandler implements NotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ButtonOnNotificationHandler.class);

    @Override
    public boolean execute(ZWaveHome home, ServerTransportMarker transport, Notification notification) {

        LOG.debug("z-wave notification : BUTTON_ON;");
        return false;
    }
}
