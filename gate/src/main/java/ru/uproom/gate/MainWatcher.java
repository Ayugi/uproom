package ru.uproom.gate;

import org.zwave4j.Notification;
import org.zwave4j.NotificationWatcher;

/**
 * Created by osipenko on 27.07.14.
 */
public class MainWatcher implements NotificationWatcher {
    @Override
    public void onNotification(Notification notification, Object o) {
        System.out.print("notification " + notification);
    }
}
