package ru.uproom.gate.notifications.zwave;

import org.zwave4j.NotificationType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by HEDIN on 12.09.2014.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ZwaveNotificationHandlerAnnotation {
    NotificationType value();
}
