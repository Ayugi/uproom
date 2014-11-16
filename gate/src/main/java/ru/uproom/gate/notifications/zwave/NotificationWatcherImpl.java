package ru.uproom.gate.notifications.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import org.zwave4j.NotificationWatcher;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.domain.ClassesSearcher;
import ru.uproom.gate.notifications.NotificationHandler;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;


/**
 * Z-Wave events watcher
 * <p/>
 * Created by osipenko on 27.07.14.
 */
@Service
public class NotificationWatcherImpl implements NotificationWatcher {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(NotificationWatcherImpl.class);

    private Map<NotificationType, NotificationHandler> notificationHandlers =
            new EnumMap<NotificationType, NotificationHandler>(NotificationType.class);

    @Autowired
    private GateDevicesSet home;


    //##############################################################################################################
    //######    constructors

    @PostConstruct
    private void prepareZwaveNotificationHandlers() {
        for (Class<?> handler : ClassesSearcher.getAnnotatedClasses(
                ZwaveNotificationHandlerAnnotation.class
        )) {
            ZwaveNotificationHandlerAnnotation annotation =
                    handler.getAnnotation(ZwaveNotificationHandlerAnnotation.class);
            if (annotation == null) continue;
            notificationHandlers.put(
                    annotation.value(),
                    (NotificationHandler) ClassesSearcher.instantiate(handler)
            );
        }
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  call notification handler

    @Override
    public void onNotification(Notification notification, Object o) {

        // find handler for notification
        NotificationHandler handler = notificationHandlers.get(notification.getType());
        if (handler == null) {
            LOG.debug("handler for notification {} not found", notification.getType().name());
            return;
        }

        // execution notification
        handler.execute(notification, home);
    }

}
