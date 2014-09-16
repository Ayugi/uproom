package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import org.zwave4j.NotificationWatcher;
import ru.uproom.gate.domain.ClassesSearcher;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.ServerTransportUser;
import ru.uproom.gate.zwave.ZWaveHome;

import java.util.EnumMap;
import java.util.Map;


/**
 * Z-Wave & gate events watcher
 * <p/>
 * Created by osipenko on 27.07.14.
 */
@Service
public class MainWatcher implements NotificationWatcher, ServerTransportUser, GateWatcher {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(MainWatcher.class);

    private Map<NotificationType, NotificationHandler> notificationHandlers =
            new EnumMap<NotificationType, NotificationHandler>(NotificationType.class);

    private Map<GateNotificationType, NotificationHandler> gateNotificationHandlers =
            new EnumMap<GateNotificationType, NotificationHandler>(GateNotificationType.class);

    private boolean PRINT_DEBUG_MESSAGES = true;
    private boolean ready = false;
    private boolean failed = false;
    private ZWaveHome home = null;

    private boolean link = false;

    @Autowired
    private ServerTransportMarker transport = null;


    //##############################################################################################################
    //######    constructors

    public MainWatcher() {
        super();
        prepareZwaveNotificationHandlers();
        prepareGateNotificationHandlers();
    }


    //##############################################################################################################
    //######    getters and setters


    //------------------------------------------------------------------------
    //  Z-Wave driver is ready for work

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean _ready) {
        if (_ready) {
            System.out.println("---- zwave network ready ----");
            setFailed(false);
        }
        ready = _ready;
    }


    //------------------------------------------------------------------------
    //  Z-Wave driver fault

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        if (failed) {
            System.out.println("---- zwave network failed ----");
            setReady(false);
        }
        this.failed = failed;
    }


    //------------------------------------------------------------------------
    //  main object containing node list

    public ZWaveHome getHome() {
        return home;
    }

    public void setHome(ZWaveHome home) {
        this.home = home;
    }


    //------------------------------------------------------------------------
    //  object which send messages to server

    public ServerTransportMarker getTransport() {
        return transport;
    }

    public void setTransport(ServerTransportMarker transport) {
        if (transport == null) link = false;
        else {
            this.transport = transport;
            link = true;
        }
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  notification handlers

    private void prepareZwaveNotificationHandlers() {
        for (Class<?> handler : ClassesSearcher.findAnnotatedClasses(ZwaveNotificationHandler.class)) {
            ZwaveNotificationHandler annotation = handler.getAnnotation(ZwaveNotificationHandler.class);
            notificationHandlers.put(annotation.value(), (NotificationHandler) ClassesSearcher.instantiate(handler));
        }
    }

    private void prepareGateNotificationHandlers() {
        for (Class<?> handler : ClassesSearcher.findAnnotatedClasses(GateNotificationHandler.class)) {
            GateNotificationHandler annotation = handler.getAnnotation(GateNotificationHandler.class);
            gateNotificationHandlers.put(annotation.value(), (NotificationHandler) ClassesSearcher.instantiate(handler));
        }
    }


    //------------------------------------------------------------------------
    //  handling inline gate events

    @Override
    public boolean onGateEvent(GateNotificationType type) {

        // find handler for notification
        NotificationHandler handler = gateNotificationHandlers.get(type);
        if (handler == null) {
            LOG.debug("[ERR] - MainWatcher - onGateEvent - handler for notification (%s) not found",
                    type.name()
            );
            return false;
        }

        // execution notification
        return handler.execute(home, link ? transport : null, null);
    }


    //------------------------------------------------------------------------
    //  диспетчер обработки событий библиотеки Z-Wave

    @Override
    public void onNotification(Notification notification, Object o) {

        // find handler for notification
        NotificationHandler handler = notificationHandlers.get(notification.getType());
        if (handler == null) {
            LOG.debug("[ERR] - MainWatcher - onNotification - handler for notification (%s) not found",
                    notification.getType().name()
            );
            return;
        }

        // execution notification
        handler.execute(home, link ? transport : null, notification);
    }

}
