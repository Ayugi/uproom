package ru.uproom.gate.notifications;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by osipenko on 12.09.2014.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GateNotificationHandlerAnnotation {
    GateNotificationType value();
}
