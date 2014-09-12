package ru.uproom.gate.notifications;

import org.zwave4j.Manager;
import org.zwave4j.Notification;
import org.zwave4j.NotificationType;
import org.zwave4j.NotificationWatcher;
import ru.uproom.gate.transport.ServerTransportMarker;
import ru.uproom.gate.transport.ServerTransportUser;
import ru.uproom.gate.zwave.ZWaveHome;
import ru.uproom.gate.zwave.ZWaveNode;
import ru.uproom.gate.zwave.ZWaveValue;
import ru.uproom.gate.zwave.ZWaveValueIndexFactory;

import java.util.TreeMap;


/**
 * Z-Wave events watcher
 * <p/>
 * Created by osipenko on 27.07.14.
 */
public class MainWatcher extends TreeMap<String, NotificationHandler>
        implements NotificationWatcher, ServerTransportUser, GateWatcher {


    //##############################################################################################################
    //######    fields


    private boolean PRINT_DEBUG_MESSAGES = true;
    private boolean ready = false;
    private boolean failed = false;
    private ZWaveHome home = null;

    private boolean link = false;
    private ServerTransportMarker transport = null;


    //##############################################################################################################
    //######    constructors


    public MainWatcher() {
        super();
        // filling map with reflection API - Z-Wave notifications
        for (NotificationType type : NotificationType.values()) {
            try {
                String name = nameConverting(type);
                Class c = Class.forName("ru.uproom.gate.notifications." + name + "NotificationHandler");
                NotificationHandler handler = (NotificationHandler) c.newInstance();
                this.put(type.name(), handler);
            } catch (ClassNotFoundException e) {
                System.out.println("[ERR CNF] - MainWatcher - constructor - " + e.getMessage());
            } catch (InstantiationException e) {
                System.out.println("[ERR INS] - MainWatcher - constructor - " + e.getMessage());
            } catch (IllegalAccessException e) {
                System.out.println("[ERR ILA] - MainWatcher - constructor - " + e.getMessage());
            }
        }
        // filling map with reflection API - gate notifications
        for (GateNotificationType type : GateNotificationType.values()) {
            try {
                Class c = Class.forName("ru.uproom.gate.notifications." + type.name() + "NotificationHandler");
                NotificationHandler handler = (NotificationHandler) c.newInstance();
                this.put(type.name(), handler);
            } catch (ClassNotFoundException e) {
                System.out.println("[ERR CNF] - MainWatcher - constructor - " + e.getMessage());
            } catch (InstantiationException e) {
                System.out.println("[ERR INS] - MainWatcher - constructor - " + e.getMessage());
            } catch (IllegalAccessException e) {
                System.out.println("[ERR ILA] - MainWatcher - constructor - " + e.getMessage());
            }
        }
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
    //  conversion names of z-wave lib notification type

    public String nameConverting(NotificationType type) {
        String result = "";

        String[] words = type.name().split("_");
        for (String word : words) {
            String newWord = word.toLowerCase();
            result += newWord.substring(0, 1).toUpperCase() + newWord.substring(1);
        }

        return result;
    }


    //------------------------------------------------------------------------
    //  handling inline gate events

    @Override
    public boolean onGateEvent(GateNotificationType type) {

        // find handler for notification
        NotificationHandler handler = this.get(type);
        if (handler == null) {
            System.out.println(String.format("[ERR] - MainWatcher - onGateEvent - handler for notification (%s) not found",
                    type.name()
            ));
            return false;
        }

        // execution notification
        return handler.execute(home, link ? transport : null);
    }


    //------------------------------------------------------------------------
    //  диспетчер обработки событий библиотеки Z-Wave

    @Override
    public void onNotification(Notification notification, Object o) {

        // find handler for notification
        NotificationHandler handler = this.get(notification.getType().name());
        if (handler == null) {
            System.out.println(String.format("[ERR] - MainWatcher - onNotification - handler for notification (%s) not found",
                    notification.getType().name()
            ));
        }

        // execution notification
        handler.execute(home, link ? transport : null);

        // Список событий
//        switch (notification.getType()) {
//            case DRIVER_READY:
//                onNotificationDriverReady(notification, o);
//                break;
//            case DRIVER_FAILED:
//                onNotificationDriverFailed(notification, o);
//                break;
//            case DRIVER_RESET:
//                onNotificationDriverReset(notification, o);
//                break;
//            case AWAKE_NODES_QUERIED:
//                onNotificationAwakeNodesQueried(notification, o);
//                break;
//            case ALL_NODES_QUERIED:
//                onNotificationAllNodesQueried(notification, o);
//                break;
//            case ALL_NODES_QUERIED_SOME_DEAD:
//                onNotificationAllNodesQueriedSomeDead(notification, o);
//                break;
//            case POLLING_ENABLED:
//                onNotificationPollingEnabled(notification, o);
//                break;
//            case POLLING_DISABLED:
//                onNotificationPollingDisabled(notification, o);
//                break;
//            case NODE_NEW:
//                onNotificationNodeNew(notification, o);
//                break;
//            case NODE_ADDED:
//                onNotificationNodeAdded(notification, o);
//                break;
//            case NODE_REMOVED:
//                onNotificationNodeRemoved(notification, o);
//                break;
//            case ESSENTIAL_NODE_QUERIES_COMPLETE:
//                onNotificationEssentialNodeQueriesComplete(notification, o);
//                break;
//            case NODE_QUERIES_COMPLETE:
//                onNotificationNodeQueriesComplete(notification, o);
//                break;
//            case NODE_EVENT:
//                onNotificationNodeEvent(notification, o);
//                break;
//            case NODE_NAMING:
//                onNotificationNodeNaming(notification, o);
//                break;
//            case NODE_PROTOCOL_INFO:
//                onNotificationNodeProtocolInfo(notification, o);
//                break;
//            case VALUE_ADDED:
//                onNotificationValueAdded(notification, o);
//                break;
//            case VALUE_REMOVED:
//                onNotificationValueRemoved(notification, o);
//                break;
//            case VALUE_CHANGED:
//                onNotificationValueChanged(notification, o);
//                break;
//            case VALUE_REFRESHED:
//                onNotificationValueRefreshed(notification, o);
//                break;
//            case GROUP:
//                onNotificationGroup(notification, o);
//                break;
//            case SCENE_EVENT:
//                onNotificationSceneEvent(notification, o);
//                break;
//            case CREATE_BUTTON:
//                onNotificationCreateButton(notification, o);
//                break;
//            case DELETE_BUTTON:
//                onNotificationDeleteButton(notification, o);
//                break;
//            case BUTTON_ON:
//                onNotificationButtonOn(notification, o);
//                break;
//            case BUTTON_OFF:
//                onNotificationButtonOff(notification, o);
//                break;
//            case NOTIFICATION:
//                onNotificationError(notification, o);
//                break;
//            default:
//                onNotificationUnknown(notification, o);
//                break;
//        }
    }


    //------------------------------------------------------------------------
    //  Z-Wave library event handlers


    // ======  driver set up successfully

    public void onNotificationDriverReady(Notification notification, Object o) {

        // HomeID in Z-Wave notation - our GateID
        getHome().setHomeId(notification.getHomeId());

        // send message to server
//        if (link) transport.sendCommand(new ChangeDeviceStateCommand(
//                String.format("%d", getHome().getHomeId()),
//                Manager.get().getControllerNodeId(getHome().getHomeId()),
//                "READY"
//        ));

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Driver ready\n" +
                        "\thome id: %d",
                notification.getHomeId()
        ));
    }


    // ======  запуск драйвера произведен неудачно

    public void onNotificationDriverFailed(Notification notification, Object o) {

        // Драйвер не поднят
        setFailed(true);

        // send message to server
//        if (link) transport.sendCommand(new ChangeDeviceStateCommand(
//                String.format("%d", getHome().getHomeId()),
//                Manager.get().getControllerNodeId(getHome().getHomeId()),
//                "FAULT"
//        ));

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Driver failed"));
    }


    // ======  состояние драйвера сброшено

    public void onNotificationDriverReset(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Driver reset"));
    }


    // ======  все узлы сети (включая ждущий режим) опрошены

    public void onNotificationAwakeNodesQueried(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Awake nodes queried"));
    }


    // ======  все узлы сети опрошены

    public void onNotificationAllNodesQueried(Notification notification, Object o) {

        // Контроллер сети готов к работе
        Manager.get().writeConfig(getHome().getHomeId());
        setReady(true);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("All nodes queried"));
    }


    // ======  все узлы сети опрошены (найдено несколько отсутствующих)

    public void onNotificationAllNodesQueriedSomeDead(Notification notification, Object o) {

        // Контроллер сети готов к работе
        Manager.get().writeConfig(getHome().getHomeId());
        setReady(true);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("All nodes queried some dead"));
    }


    // ======  активирован периодический опрос параметров узла

    public void onNotificationPollingEnabled(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Polling enabled"));
    }


    // ======  отключен периодический опрос параметров узла

    public void onNotificationPollingDisabled(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Polling disabled"));
    }


    // ======  создан новый узел

    public void onNotificationNodeNew(Notification notification, Object o) {

        // Определение идентификатора узла и добавление его в список известных узлов
        ZWaveNode node = new ZWaveNode(getHome(), notification.getNodeId());
        getHome().put(node.getZId(), node);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Node new\n" +
                        "\tnode id: %d",
                notification.getNodeId()
        ));
    }


    // ======  добавление нового устройства в сеть Z-Wave

    public void onNotificationNodeAdded(Notification notification, Object o) {

        // Определение идентификатора узла и добавление его в список известных узлов
        ZWaveNode node = new ZWaveNode(getHome(), notification.getNodeId());
        getHome().put(node.getZId(), node);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Node added\n" +
                        "\tnode id: %d" +
                        "\tnode type: %s",
                node.getZId(),
                node.getNodeType()
        ));
    }


    // ======  удаление существующего устройства из сети Z-Wave

    public void onNotificationNodeRemoved(Notification notification, Object o) {

        // Определение идентификатора узла и удаление его из списка известных узлов
        short nodeId = notification.getNodeId();
        getHome().remove(nodeId);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Node removed\n" +
                        "\tnode id: %d" +
                        "\tnode type: %s",
                nodeId,
                Manager.get().getNodeType(getHome().getHomeId(), nodeId)
        ));
    }


    // ======  ???

    public void onNotificationEssentialNodeQueriesComplete(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Node essential queries complete\n" +
                        "\tnode id: %d",
                notification.getNodeId()
        ));
    }


    // ======  все запросы к устройству обработаны

    public void onNotificationNodeQueriesComplete(Notification notification, Object o) {

        // вызов обработчиков событий для данного узла
        ZWaveNode node = getHome().get(notification.getNodeId());
        if (node == null) return;
        node.callEvents(notification);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Node queries complete\n" +
                        "\tnode id: %d",
                notification.getNodeId()
        ));
    }


    // ======  оповещение о событии на узле

    public void onNotificationNodeEvent(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Node event\n" +
                        "\tnode id: %d\n" +
                        "\tevent id: %d",
                notification.getNodeId(),
                notification.getEvent()
        ));
    }


    // ======  изменились идентификационные параметры узла (имя, номер продукта, производитель etc.)

    public void onNotificationNodeNaming(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Node naming\n" +
                        "\tnode id: %d",
                notification.getNodeId()
        ));
    }


    // ======  изменилась информация о протоколе обмена данными с узлом

    public void onNotificationNodeProtocolInfo(Notification notification, Object o) {

        // Определение идентификатора узла
        short nodeId = notification.getNodeId();

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Node protocol info\n" +
                        "\tnode id: %d\n" +
                        "\ttype: %s",
                nodeId,
                Manager.get().getNodeType(getHome().getHomeId(), nodeId)
        ));
    }


    // ======  добавлен новый параметр узла

    public void onNotificationValueAdded(Notification notification, Object o) {

        // находим узел в который добавляется параметр
        ZWaveNode node = getHome().get(notification.getNodeId());
        if (node == null) return;

        // добавляем параметр
        ZWaveValue value = new ZWaveValue();
        value.setValueId(notification.getValueId());
        Integer index = ZWaveValueIndexFactory.createIndex(
                value.getValueCommandClass(),
                value.getValueInstance(),
                value.getValueIndex()
        );
        node.put(index, value);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Value added\n" +
                        "\tnode id: %d\n" +
                        "\tcommand class: %d\n" +
                        "\tinstance: %d\n" +
                        "\tindex: %d\n" +
                        "\tgenre: %s\n" +
                        "\ttype: %s\n" +
                        "\tlabel: %s\n" +
                        "\tvalue: %s",
                notification.getNodeId(),
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex(),
                notification.getValueId().getGenre().name(),
                notification.getValueId().getType().name(),
                Manager.get().getValueLabel(notification.getValueId()),
                value.getValueAsString()
        ));
    }


    // ======  удален существующий параметр узла

    public void onNotificationValueRemoved(Notification notification, Object o) {

        // находим узел из которого удаляется параметр
        ZWaveNode node = home.get(notification.getNodeId());
        if (node == null) return;

        // удаляем параметр
        Integer index = ZWaveValueIndexFactory.createIndex(
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex()
        );
        node.remove(index);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Value removed\n" +
                        "\tnode id: %d\n" +
                        "\tcommand class: %d\n" +
                        "\tinstance: %d\n" +
                        "\tindex: %d",
                notification.getNodeId(),
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex()
        ));
    }


    // ======  изменено значение параметра узла

    public void onNotificationValueChanged(Notification notification, Object o) {

        // находим узел параметр которого обновился
        ZWaveNode node = getHome().get(notification.getNodeId());
        if (node == null) return;

        // находим параметр
        Integer index = ZWaveValueIndexFactory.createIndex(
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex()
        );
        ZWaveValue value = node.get(index);
        if (value == null) return;

        // если есть подписчики, оповещаем их
        value.callEvents();

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Value changed\n" +
                        "\tnode id: %d\n" +
                        "\tlabel: %s\n" +
                        "\tcommand class: %d\n" +
                        "\tinstance: %d\n" +
                        "\tindex: %d\n" +
                        "\tvalue: %s",
                notification.getNodeId(),
                Manager.get().getValueLabel(notification.getValueId()),
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex(),
                value.getValueAsString()
        ));
    }


    // ======  обновлено значение параметра узла

    public void onNotificationValueRefreshed(Notification notification, Object o) {

        // находим узел параметр которого обновился
        ZWaveNode node = getHome().get(notification.getNodeId());
        if (node == null) return;

        // находим параметр
        Integer index = ZWaveValueIndexFactory.createIndex(
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex()
        );
        ZWaveValue value = node.get(index);
        if (value == null) return;

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Value refreshed\n" +
                        "\tnode id: %d\n" +
                        "\tcommand class: %d\n" +
                        "\tinstance: %d\n" +
                        "\tindex: %d" +
                        "\tvalue: %s",
                notification.getNodeId(),
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex(),
                value.getValueAsString()
        ));
    }


    // ======  событие связанное с группой устройств

    public void onNotificationGroup(Notification notification, Object o) {

        // узел связанный с группой
        ZWaveNode node = getHome().get(notification.getNodeId());
        if (node == null) return;

        // Если такой группы еще не было, добавляем ее
        if (!node.existGroup(notification.getGroupIdx()))
            node.addGroup(notification.getGroupIdx());

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Group\n" +
                        "\tnode id: %d\n" +
                        "\tgroup id: %d",
                notification.getNodeId(),
                notification.getGroupIdx()
        ));
    }


    // ======  событие связанное с активацией сценария

    public void onNotificationSceneEvent(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Scene event\n" +
                        "\tscene id: %d",
                notification.getSceneId()
        ));
    }


    // ======  создан новый элемент управления

    public void onNotificationCreateButton(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Button create\n" +
                        "\tbutton id: %d",
                notification.getButtonId()
        ));
    }


    // ======  удален существующий элемент управления

    public void onNotificationDeleteButton(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Button delete\n" +
                        "\tbutton id: %d",
                notification.getButtonId()
        ));
    }


    // ======  активирован элемент управления

    public void onNotificationButtonOn(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Button on\n" +
                        "\tbutton id: %d",
                notification.getButtonId()
        ));
    }


    // ======  отключен элемент управления

    public void onNotificationButtonOff(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Button off\n" +
                        "\tbutton id: %d",
                notification.getButtonId()
        ));
    }


    // ======  аварийное событие в сети

    public void onNotificationError(Notification notification, Object o) {
        String notificationCode = "";

        // вызвавший событие узел
        ZWaveNode node = getHome().get(notification.getNodeId());
        if (node == null) return;

        // вызов обработчиков событий для данного узла
        node.callEvents(notification);

        // обработка кодов оповещений
        switch (notification.getNotification()) {

            case 0: // MSG_COMPLETE
                notificationCode = "MSG_COMPLETE";
                break;

            case 1: // TIMEOUT
                notificationCode = "TIMEOUT";
                break;

            case 2: // NO_OPERATION
                notificationCode = "NO_OPERATION";
                break;

            case 3: // AWAKE
                notificationCode = "AWAKE";
                break;

            case 4: // SLEEP
                notificationCode = "SLEEP";
                break;

            case 5: // DEAD
                notificationCode = "DEAD";
                onNotificationNodeRemoved(notification, new Object());
                break;

            case 6: // ALIVE
                notificationCode = "ALIVE";
                onNotificationNodeAdded(notification, new Object());
                break;

            default:
                notificationCode = String.format("UNKNOWN (%d)", notification.getNotification());

        }


        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Error Notification\n" +
                        "\thome id: %d" +
                        "\tnode id: %d" +
                        "\tnotify : %s",
                notification.getHomeId(),
                notification.getNodeId(),
                notificationCode
        ));
    }


    // ======  необрабатываемое событие

    public void onNotificationUnknown(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format("Unhandled Notification : " +
                        notification.getType().name()
        ));
    }

}
