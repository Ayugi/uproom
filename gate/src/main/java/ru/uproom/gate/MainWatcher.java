package ru.uproom.gate;

import org.zwave4j.Manager;
import org.zwave4j.Notification;
import org.zwave4j.NotificationWatcher;
import org.zwave4j.ValueId;

import java.util.HashMap;

/**
 *
 * Реализация класса обработки событий библиотеки управления сетью устройств Z-Wave
 *
 * Created by osipenko on 27.07.14.
 */
public class MainWatcher implements NotificationWatcher {


    //##############################################################################################################
    //######    параметры класса

    private static boolean PRINT_DEBUG_MESSAGES = true;
    private static long homeId;
    private static boolean ready = false;
    private static Manager manager = null;
    private static HashMap<Short, ZwaveNode> nodes = new HashMap<Short, ZwaveNode>();


    //##############################################################################################################
    //######    обработка параметров класса


    //------------------------------------------------------------------------
    //  Объект управления сетью устройств Z-Wave

    public boolean setManager(Manager _manager) {
        manager = _manager;
        return _manager != null;
    }

    public Manager getManager() {
        return manager;
    }


    //------------------------------------------------------------------------
    //  Признак готовности к работе драйвера контроллера

    public boolean getReady() {
        return ready;
    }

    public void setReady(boolean _ready) {
        if (_ready) System.out.println("---- zwave network ready ----");
        ready = _ready;
    }


    //------------------------------------------------------------------------
    //  Идентификатор совокупности помещений (дома) ассоциированного с сетью

    public long getHomeId() {
        return homeId;
    }

    public void setHomeId(long _homeId) {
        homeId = _homeId;
    }


    //##############################################################################################################
    //######    методы класса-

    private String getValue(ValueId _vaValueId) {
        return "=value=";
    }


    //------------------------------------------------------------------------
    //  диспетчер обработки событий библиотеки Z-Wave

    @Override
    public void onNotification(Notification notification, Object o) {

        // Менеджер должен быть установлен до того, как начнут обрабатываться события
        if (manager == null) {
            System.out.println("Manager not set");
            return;
        }

        // Список событий
        switch (notification.getType()) {
            case DRIVER_READY:
                onNotificationDriverReady(notification, o);
                break;
            case DRIVER_FAILED:
                onNotificationDriverFailed(notification, o);
                break;
            case DRIVER_RESET:
                onNotificationDriverReset(notification, o);
                break;
            case AWAKE_NODES_QUERIED:
                onNotificationAwakeNodesQueried(notification, o);
                break;
            case ALL_NODES_QUERIED:
                onNotificationAllNodesQueried(notification, o);
                break;
            case ALL_NODES_QUERIED_SOME_DEAD:
                onNotificationAllNodesQueriedSomeDead(notification, o);
                break;
            case POLLING_ENABLED:
                onNotificationPollingEnabled(notification, o);
                break;
            case POLLING_DISABLED:
                onNotificationPollingDisabled(notification, o);
                break;
            case NODE_NEW:
                onNotificationNodeNew(notification, o);
                break;
            case NODE_ADDED:
                onNotificationNodeAdded(notification, o);
                break;
            case NODE_REMOVED:
                onNotificationNodeRemoved(notification, o);
                break;
            case ESSENTIAL_NODE_QUERIES_COMPLETE:
                onNotificationEssentialNodeQueriesComplete(notification, o);
                break;
            case NODE_QUERIES_COMPLETE:
                onNotificationNodeQueriesComplete(notification, o);
                break;
            case NODE_EVENT:
                onNotificationNodeEvent(notification, o);
                break;
            case NODE_NAMING:
                onNotificationNodeNaming(notification, o);
                break;
            case NODE_PROTOCOL_INFO:
                onNotificationNodeProtocolInfo(notification, o);
                break;
            case VALUE_ADDED:
                onNotificationValueAdded(notification, o);
                break;
            case VALUE_REMOVED:
                onNotificationValueRemoved(notification, o);
                break;
            case VALUE_CHANGED:
                onNotificationValueChanged(notification, o);
                break;
            case VALUE_REFRESHED:
                onNotificationValueRefreshed(notification, o);
                break;
            case GROUP:
                onNotificationGroup(notification, o);
                break;
            case SCENE_EVENT:
                onNotificationSceneEvent(notification, o);
                break;
            case CREATE_BUTTON:
                onNotificationCreateButton(notification, o);
                break;
            case DELETE_BUTTON:
                onNotificationDeleteButton(notification, o);
                break;
            case BUTTON_ON:
                onNotificationButtonOn(notification, o);
                break;
            case BUTTON_OFF:
                onNotificationButtonOff(notification, o);
                break;
            case NOTIFICATION:
                onNotificationError(notification, o);
                break;
            default:
                onNotificationUnknown(notification, o);
                break;
        }
    }


    //------------------------------------------------------------------------
    //  События библиотеки Z-Wave


    // ======  запуск драйвера успешно произведен

    public void onNotificationDriverReady(Notification notification, Object o) {

        // получение и установка идентификатора дома
        setHomeId(notification.getHomeId());

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Driver ready\n" +
                        "\thome id: %d",
                notification.getHomeId()
        ));
    }


    // ======  запуск драйвера произведен неудачно

    public void onNotificationDriverFailed(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Driver failed"
        ));
    }


    // ======  состояние драйвера сброшено

    public void onNotificationDriverReset(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Driver reset"
        ));
    }


    // ======  все узлы сети (включая ждущий режим) опрошены

    public void onNotificationAwakeNodesQueried(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Awake nodes queried"
        ));
    }


    // ======  все узлы сети опрошены

    public void onNotificationAllNodesQueried(Notification notification, Object o) {

        // Контроллер сети готов к работе
        getManager().writeConfig(getHomeId());
        setReady(true);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "All nodes queried"
        ));
    }


    // ======  все узлы сети опрошены (найдено несколько отсутствующих)

    public void onNotificationAllNodesQueriedSomeDead(Notification notification, Object o) {

        // Контроллер сети готов к работе
        getManager().writeConfig(getHomeId());
        setReady(true);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "All nodes queried some dead"
        ));
    }


    // ======  активирован периодический опрос параметров узла

    public void onNotificationPollingEnabled(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Polling enabled"
        ));
    }


    // ======  отключен периодический опрос параметров узла

    public void onNotificationPollingDisabled(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Polling disabled"
        ));
    }


    // ======  создан новый узел

    public void onNotificationNodeNew(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Node new\n" +
                        "\tnode id: %d",
                notification.getNodeId()
        ));
    }


    // ======  добавление нового устройства в сеть Z-Wave

    public void onNotificationNodeAdded(Notification notification, Object o) {

        // Определение идентификатора узла и добавление его в список известных узлов
        short nodeId = notification.getNodeId();
        nodes.put(nodeId, new ZwaveNode());

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Node added\n" +
                        "\tnode id: %d" +
                        "\tnode type: %s",
                nodeId,
                manager.getNodeType(homeId, nodeId)
        ));
    }


    // ======  удаление существующего устройства из сети Z-Wave

    public void onNotificationNodeRemoved(Notification notification, Object o) {

        // Определение идентификатора узла и удаление его из списка известных узлов
        short nodeId = notification.getNodeId();
        nodes.remove(nodeId);

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Node removed\n" +
                        "\tnode id: %d" +
                        "\tnode type: %s",
                nodeId,
                manager.getNodeType(homeId, nodeId)
        ));
    }


    // ======  ???

    public void onNotificationEssentialNodeQueriesComplete(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Node essential queries complete\n" +
                        "\tnode id: %d",
                notification.getNodeId()
        ));
    }


    // ======  все запросы к устройству обработаны

    public void onNotificationNodeQueriesComplete(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Node queries complete\n" +
                        "\tnode id: %d",
                notification.getNodeId()
        ));
    }


    // ======  оповещение о событии на узле

    public void onNotificationNodeEvent(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Node event\n" +
                        "\tnode id: %d\n" +
                        "\tevent id: %d",
                notification.getNodeId(),
                notification.getEvent()
        ));
    }


    // ======  изменились идентификационные параметры узла (имя, номер продукта, производитель etc.)

    public void onNotificationNodeNaming(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Node naming\n" +
                        "\tnode id: %d",
                notification.getNodeId()
        ));
    }


    // ======  изменилась информация о протоколе обмена данными с узлом

    public void onNotificationNodeProtocolInfo(Notification notification, Object o) {

        // Определение идентификатора узла
        short nodeId = notification.getNodeId();

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Node protocol info\n" +
                        "\tnode id: %d\n" +
                        "\ttype: %s",
                nodeId,
                manager.getNodeType(notification.getHomeId(), nodeId)
        ));
    }


    // ======  добавлен новый параметр узла

    public void onNotificationValueAdded(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Value added\n" +
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
                manager.getValueLabel(notification.getValueId()),
                getValue(notification.getValueId())
        ));
    }


    // ======  удален существующий параметр узла

    public void onNotificationValueRemoved(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Value removed\n" +
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

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Value changed\n" +
                        "\tnode id: %d\n" +
                        "\tcommand class: %d\n" +
                        "\tinstance: %d\n" +
                        "\tindex: %d\n" +
                        "\tvalue: %s",
                notification.getNodeId(),
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex(),
                getValue(notification.getValueId())
        ));
    }


    // ======  обновлено значение параметра узла

    public void onNotificationValueRefreshed(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Value refreshed\n" +
                        "\tnode id: %d\n" +
                        "\tcommand class: %d\n" +
                        "\tinstance: %d\n" +
                        "\tindex: %d" +
                        "\tvalue: %s",
                notification.getNodeId(),
                notification.getValueId().getCommandClassId(),
                notification.getValueId().getInstance(),
                notification.getValueId().getIndex(),
                getValue(notification.getValueId())
        ));
    }


    // ======  событие связанное с группой устройств

    public void onNotificationGroup(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Group\n" +
                        "\tnode id: %d\n" +
                        "\tgroup id: %d",
                notification.getNodeId(),
                notification.getGroupIdx()
        ));
    }


    // ======  событие связанное с активацией сценария

    public void onNotificationSceneEvent(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Scene event\n" +
                        "\tscene id: %d",
                notification.getSceneId()
        ));
    }


    // ======  создан новый элемент управления

    public void onNotificationCreateButton(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Button create\n" +
                        "\tbutton id: %d",
                notification.getButtonId()
        ));
    }


    // ======  удален существующий элемент управления

    public void onNotificationDeleteButton(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Button delete\n" +
                        "\tbutton id: %d",
                notification.getButtonId()
        ));
    }


    // ======  активирован элемент управления

    public void onNotificationButtonOn(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Button on\n" +
                        "\tbutton id: %d",
                notification.getButtonId()
        ));
    }


    // ======  отключен элемент управления

    public void onNotificationButtonOff(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Button off\n" +
                        "\tbutton id: %d",
                notification.getButtonId()
        ));
    }


    // ======  аварийное событие в сети

    public void onNotificationError(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Error Notification\n" +
                        "\thome id: %d" +
                        "\tnode id: %d",
                notification.getHomeId(),
                notification.getNodeId()
        ));
    }


    // ======  необрабатываемое событие

    public void onNotificationUnknown(Notification notification, Object o) {

        // вывод отладочной информации
        if (PRINT_DEBUG_MESSAGES) System.out.println(String.format(
                "Unhandled Notification : " +
                        notification.getType().name()
        ));
    }

}
