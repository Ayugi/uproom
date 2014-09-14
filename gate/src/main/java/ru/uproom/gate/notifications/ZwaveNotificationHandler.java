package ru.uproom.gate.notifications;

import org.zwave4j.NotificationType;

/**
 * Created by HEDIN on 12.09.2014.
 */
public @interface ZwaveNotificationHandler {
    NotificationType value();
}
