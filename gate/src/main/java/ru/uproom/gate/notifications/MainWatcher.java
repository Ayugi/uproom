package ru.uproom.gate.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import org.zwave4j.NotificationWatcher;
import ru.uproom.gate.domain.ClassesSearcher;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.ServerTransportUser;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.zwave.ZWaveHome;

import java.util.EnumMap;
import java.util.Map;


/**
 * Z-Wave & gate events watcher
 * <p/>
 * Created by osipenko on 27.07.14.
 */
//@Service
public class MainWatcher implements NotificationWatcher, ServerTransportUser, GateWatcher {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(MainWatcher.class);

    private Map<NotificationType, NotificationHandler> notificationHandlers =
            new EnumMap<NotificationType, NotificationHandler>(NotificationType.class);

    private Map<GateNotificationType, NotificationHandler> gateNotificationHandlers =
            new EnumMap<GateNotificationType, NotificationHandler>(GateNotificationType.class);

    private boolean PRINT_DEBUG_MESSAGES = true;
    private ZWaveHome home;

    private boolean link = false;
    private boolean doExit;

    //@Autowired
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
    //  main object containing node list

    @Override // GateWatcher
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

    @Override // ServerTransportUser
    public void setTransport(ServerTransportMarker transport) {
        if (transport == null) link = false;
        else {
            this.transport = transport;
            link = true;
            // send handshake every time when link with server established
            onGateEvent(GateNotificationType.Handshake, null);
        }
    }


    //------------------------------------------------------------------------
    //  flag of shutdown

    public boolean isDoExit() {
        return doExit;
    }

    public void setDoExit(boolean doExit) {
        this.doExit = doExit;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  notification handlers

    private void prepareZwaveNotificationHandlers() {
        for (Class<?> handler : ClassesSearcher.getAnnotatedClasses(
                "ru.uproom.gate.notifications",
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

    private void prepareGateNotificationHandlers() {
        for (Class<?> handler : ClassesSearcher.getAnnotatedClasses(
                "ru.uproom.gate.notifications",
                GateNotificationHandlerAnnotation.class
        )) {
            GateNotificationHandlerAnnotation annotation =
                    handler.getAnnotation(GateNotificationHandlerAnnotation.class);
            if (annotation == null) continue;
            gateNotificationHandlers.put(
                    annotation.value(),
                    (NotificationHandler) ClassesSearcher.instantiate(handler)
            );
        }
    }


    //------------------------------------------------------------------------
    //  handling inline gate events

    @Override
    public boolean onGateEvent(GateNotificationType type, DeviceDTO device) {

        // shutdown gate
        if (type == GateNotificationType.Shutdown) setDoExit(true);

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
