package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.domain.ClassesSearcher;
import ru.uproom.gate.notifications.zwave.NotificationWatcherImpl;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;


/**
 * Main object for handling server commands
 * </p>
 * Created by osipenko on 05.08.14.
 */
@Service
public class GateCommanderImpl implements GateCommander {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(NotificationWatcherImpl.class);

    private Map<CommandType, CommandHandler> commandHandlers =
            new EnumMap<CommandType, CommandHandler>(CommandType.class);

    @Autowired
    private GateDevicesSet home;


    //##############################################################################################################
    //######    constructors


    @PostConstruct
    private void prepareCommandHandlers() {

        // todo : переделать с использованием варианта загрузки из jar-файла (пример: Пишем свой загрузчик JAVA-классов)
        if (!getCommandHandlersFromPath())
            getCommandHandlersFromJar();

    }

    private boolean getCommandHandlersFromPath() {

        for (Class<?> handler : ClassesSearcher.getAnnotatedClasses(
                CommandHandlerAnnotation.class
        )) {
            CommandHandlerAnnotation annotation =
                    handler.getAnnotation(CommandHandlerAnnotation.class);
            if (annotation == null) continue;
            commandHandlers.put(
                    annotation.value(),
                    (CommandHandler) ClassesSearcher.instantiate(handler)
            );
        }

        return commandHandlers.isEmpty();
    }

    private boolean getCommandHandlersFromJar() {

        return commandHandlers.isEmpty();
    }


    //##############################################################################################################
    //######    getters / setters


    //------------------------------------------------------------------------
    //  Z-Wave nodes

    public GateDevicesSet getHome() {
        return home;
    }

    public void setHome(GateDevicesSet home) {
        this.home = home;
    }


    //##############################################################################################################
    //######    methods-


    //------------------------------------------------------------------------
    //  executioner of commands from server

    public boolean execute(Command command) {

        CommandHandler handler = commandHandlers.get(command.getType());
        if (handler == null) {
            LOG.error("Handler for command '{}' not found", command.getType());
            return false;
        }
        if (command.getType() == CommandType.Ping) handler.execute(command, home);
        else if (home.isReady()) handler.execute(command, home);
        else return false;

        return true;
    }

// todo : move out commented code and all code below after implementation all commands
//    public ZWaveFeedback execute(ZWaveCommand command) {
//
//        ZWaveFeedback feedback = null;
//
//        // Команда завершения работы шлюза
//        if (command.getCommand().equalsIgnoreCase("quit")) {
//            setExit(true);
//            feedback = new ZWaveFeedback();
//            feedback.setFeedback("quit ( ок )");
//            return feedback;
//        }
//
//        // получение статистики драйвера ( get driver statistics )
//        if (command.getCommand().equalsIgnoreCase("get driver statistics"))
//            feedback = commandGetDriverStatistics(command);
//            // перезапуск драйвера ( restart controller )
//        else if (command.getCommand().equalsIgnoreCase("restart controller"))
//            feedback = commandRestartController(command);
//
//            // команда включения всех устройств ( switch all on )
//        else if (command.getCommand().equalsIgnoreCase("switch all on")) feedback = commandSwitchAllOn(command);
//            // команда отключения всех устройств ( switch all off )
//        else if (command.getCommand().equalsIgnoreCase("switch all off")) feedback = commandSwitchAllOff(command);
//
//            // добавление новых устройств ( add mode )
//        else if (command.getCommand().equalsIgnoreCase("add mode")) feedback = commandAddNode(command);
//            // Удаление существующих устройств ( remove mode )
//        else if (command.getCommand().equalsIgnoreCase("remove mode")) feedback = commandRemoveNode(command);
//            // удаление несуществующих устройств ( remove failed node )
//        else if (command.getCommand().equalsIgnoreCase("remove failed node"))
//            feedback = commandRemoveFailedNode(command);
//            // проверка состояния сети ( test network )
//        else if (command.getCommand().equalsIgnoreCase("test network")) feedback = commandTestNetwork(command);
//            // прерывание исполняющейся команды ( cancelRequest )
//        else if (command.getCommand().equals("cancelRequest")) feedback = commandCancel(command);
//
//            // Получение идентификатора контроллера сети ( get controller node )
//        else if (command.getCommand().equals("get controller node")) feedback = commandControllerNodeId(command);
//            // Получение списка узлов сети ( get node list )
//        else if (command.getCommand().equals("get node list")) feedback = commandGetNodeList(command);
//            // Получение информации об узле сети ( get node, nodeId=# )
//        else if (command.getCommand().equals("get node")) feedback = commandGetNode(command);
//            // Проверка неисправного состояния узла сети ( is node failed, nodeId=# )
//        else if (command.getCommand().equals("is node failed")) feedback = commandIsNodeFailed(command);
//            // Обновление информации об узле сети ( refresh node, nodeId=# )
//        else if (command.getCommand().equals("refresh node")) feedback = commandRefreshNodeInfo(command);
//            // Запрос информации о состоянии узла сети ( node state, nodeId=# )
//        else if (command.getCommand().equals("node state")) feedback = commandRequestNodeState(command);
//            // Включение узла сети ( set node on, nodeId=# )
//        else if (command.getCommand().equals("set node on")) feedback = commandSetNodeOn(command);
//            // Включение узла сети ( set node off, nodeId=# )
//        else if (command.getCommand().equals("set node off")) feedback = commandSetNodeOff(command);
//            // Включение узла сети ( set node level, nodeId=#, level=# )
//        else if (command.getCommand().equals("set node level")) feedback = commandSetNodeLevel(command);
//
//            // Получение списка параметров узла ( get value list, nodeId=# )
//        else if (command.getCommand().equals("get value list")) feedback = commandGetValueList(command);
//            // Получение значения параметра узла ( get value, nodeId=#, valueId=# )
//        else if (command.getCommand().equals("get value")) feedback = commandGetValue(command);
//            // Установка значения параметра узла ( set value, nodeId=#, valueId=#, value=# )
//        else if (command.getCommand().equals("set value")) feedback = commandSetValue(command);
//
//            // ассоциирование двух узлов между собой ( add association, nodeId=#, targetId=#, groupId=#)
//        else if (command.getCommand().equals("add association")) feedback = commandAddAssociation(command);
//            // удаление ассоциации двух узлов между собой ( remove association, nodeId=#, targetId=#, groupId=# )
//        else if (command.getCommand().equals("remove association")) feedback = commandRemoveAssociation(command);
//            // список ассоциаций узла ( get associations, nodeId=#, groupId=# )
//        else if (command.getCommand().equals("get associations")) feedback = commandGetAssociations(command);
//            // ассоциирование двух узлов между собой ( get max associations, nodeId=#, groupId=# )
//        else if (command.getCommand().equals("get max associations")) feedback = commandGetMaxAssociation(command);
//            // количество групп узла ( get groups number, nodeId=# )
//        else if (command.getCommand().equals("get groups number")) feedback = commandGetNumGroups(command);
//
//
//        return feedback;
//
//    }


    //------------------------------------------------------------------------
    //  Обработка команд сети устройств Z-Wave


    // ======  команда - Включить все узлы


    // ======  команда - Добавить новый узел
//
//    public ZWaveFeedback commandAddNode(ZWaveCommand command) {
//        final ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // создаем триггер для события
//        ControllerCallback callback = new ControllerCallback() {
//            @Override
//            public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {
//
//                if (controllerState == ControllerState.WAITING && controllerError == ControllerError.NONE)
//                    feedback.setCreated(true);
//                System.out.println(String.format(
//                        "Add mode\n" +
//                                "\tcontroller in state: %s" +
//                                "\tcontroller error: %s",
//                        controllerState,
//                        controllerError
//                ));
//            }
//        };
//
//        if (Manager.get().beginControllerCommand(watcher.getHome().getHomeId(), ControllerCommand.ADD_DEVICE, callback)) {
//
//            // ожидаем срабатывания триггера (~ 2сек)
//            int count = 0;
//            while (!feedback.isCreated() && count < 40) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException ex) {
//                    Thread.currentThread().interrupt();
//                }
//                count++;
//            }
//            // Получаем новое значение параметра для ответа
//            if (feedback.isCreated())
//                feedback.setFeedback("{\"point\":\"addMode\",\"message\":\"ok\"}");
//            else
//                feedback.setFeedback("{\"errorPoint\":\"addMode\",\"errorCode\":\"99\",\"errorMessage\":\"set mode timeout\" }");
//
//        } else
//            feedback.setFeedback("{\"errorPoint\":\"addMode\",\"errorCode\":\"98\",\"errorMessage\":\"mode not set\" }");
//
//        return feedback;
//    }
//
//
//    // ======  команда - удалить существующий узел
//
//    public ZWaveFeedback commandRemoveNode(ZWaveCommand command) {
//        final ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // создаем триггер для события
//        ControllerCallback callback = new ControllerCallback() {
//            @Override
//            public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {
//
//                if (controllerState == ControllerState.WAITING && controllerError == ControllerError.NONE)
//                    feedback.setCreated(true);
//                System.out.println(String.format(
//                        "remove mode\n" +
//                                "\tcontroller in state: %s" +
//                                "\tcontroller error: %s",
//                        controllerState,
//                        controllerError
//                ));
//            }
//        };
//
//        if (Manager.get().beginControllerCommand(watcher.getHome().getHomeId(), ControllerCommand.REMOVE_DEVICE, callback)) {
//
//            // ожидаем срабатывания триггера (~ 2сек)
//            int count = 0;
//            while (!feedback.isCreated() && count < 40) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException ex) {
//                    Thread.currentThread().interrupt();
//                }
//                count++;
//            }
//            // Получаем новое значение параметра для ответа
//            if (count < 40)
//                feedback.setFeedback("{\"errorPoint\":\"removeMode\",\"errorCode\":\"99\",\"errorMessage\":\"set mode timeout\" }");
//            else
//                feedback.setFeedback("{\"point\":\"removeMode\",\"message\":\"ok\"}");
//
//        } else
//            feedback.setFeedback("{\"errorPoint\":\"removeMode\",\"errorCode\":\"98\",\"errorMessage\":\"mode not set\" }");
//
//        return feedback;
//    }
//
//
//    // ======  команда - удалить неисправный узел
//
//    public ZWaveFeedback commandRemoveFailedNode(ZWaveCommand command) {
//        ZWaveFeedback feedback = new ZWaveFeedback();
//
//        if (Manager.get().beginControllerCommand(watcher.getHome().getHomeId(),
//                ControllerCommand.REMOVE_FAILED_NODE,
//                new ControllerCallback() {
//                    @Override
//                    public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {
//                        System.out.println(String.format(
//                                "Remove mode\n" +
//                                        "\tcontroller in state: %s" +
//                                        "\tcontroller error: %s",
//                                controllerState,
//                                controllerError
//                        ));
//                    }
//                })) feedback.setFeedback("{\"point\":\"removeFailedNode\",\"message\":\"ok\"}");
//
//        else
//            feedback.setFeedback("{\"errorPoint\":\"removeFailedNode\",\"errorCode\":\"98\",\"errorMessage\":\"remove not accepted\" }");
//
//        return feedback;
//    }
//
//
//    // ======  команда - Отмена текущей команды
//
//    public ZWaveFeedback commandCancel(ZWaveCommand command) {
//        ZWaveFeedback feedback = new ZWaveFeedback();
//
//        if (Manager.get().cancelControllerCommand(watcher.getHome().getHomeId()))
//            feedback.setFeedback("{\"point\":\"cancelRequest\",\"message\":\"ok\"}");
//        else
//            feedback.setFeedback("{\"errorPoint\":\"cancelRequest\",\"errorCode\":\"98\",\"errorMessage\":\"cancelRequest not accepted\" }");
//
//        return feedback;
//    }
//
//
//    // ======  команда - Запрос состояния узла сети
//
//    public ZWaveFeedback commandRequestNodeState(ZWaveCommand command) {
//        final ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // Проверка корректности данных команды
//        ZWaveNode node = home.getNodes().get(command.getNodeId());
//        if (node == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"requestNodeState\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
//                    command.getNodeId()
//            ));
//            return feedback;
//        }
//
//
//        // формирование реакции на событие
//        ZWaveNodeCallback callback = new ZWaveNodeCallback() {
//            @Override
//            public void onCallback(ZWaveNode node, Notification notification) {
//                // Если в процессе возник таймаут (1 == TIMEOUT)
//                if (notification.getType() == NotificationType.NOTIFICATION && notification.getNotification() == 1) {
//                    feedback.setFeedback(String.format("{\"errorPoint\":\"requestNodeState\",\"errorCode\":\"4\",\"errorMessage\":\"node %d disconnected\"}",
//                            node.getZId()
//                    ));
//                    feedback.setSuccessful(false);
//                }
//                // Если возникло событие NODE_QUERIES_COMPLETE
//                else if (notification.getType() == NotificationType.NODE_QUERIES_COMPLETE) {
//                    feedback.setFeedback(String.format("{\"point\":\"requestNodeState\",\"message\":\"node %d request complete\"}",
//                            node.getZId()
//                    ));
//                    feedback.setSuccessful(true);
//                }
//                // Необрабатываемое событие
//                else {
//                    feedback.setFeedback(String.format("{\"point\":\"requestNodeState\",\"message\":\"node %d send notification %s\"}",
//                            node.getZId(),
//                            notification.getType().toString()
//                    ));
//                }
//                // ответ получен
//                feedback.setCreated(true);
//            }
//        };
//        node.addEvent(callback);
//
//        // запрос состояния удачен
//        if (Manager.get().requestNodeState(getHome().getHomeId(), node.getZId())) {
//            // ожидаем срабатывания триггера изменении параметра (~ 2сек)
//            int count = 0;
//            while (!feedback.isCreated() && count < 40) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException ex) {
//                    Thread.currentThread().interrupt();
//                }
//                count++;
//            }
//            // В случае, если проверяемым узлом был контроллер сети, перезапускаем его
////            if (!feedback.isSuccessful() && Manager.get().getControllerNodeId(getHome().getHomeId()) == node.getZId())
////                getWatcher().setFailed(true);
//        }
//        // Запрос состояния неудачен
//        else {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"requestNodeState\",\"errorCode\":\"97\"\"errorMessage\":\"node %d not requested\"}",
//                    node.getZId()
//            ));
//        }
//        // закрываем триггер
//        node.removeEvent(callback);
//
//        return feedback;
//    }
//
//
//    // ======  команда - Установка значения параметра узла
//
//    public ZWaveFeedback commandSetValue(final ZWaveCommand command) {
//        final ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // проверка корректности параметров команды
//        ZWaveNode node = home.getNodes().get(command.getNodeId());
//        if (node == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"setValue\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
//                    command.getNodeId()
//            ));
//            return feedback;
//        }
//        ZWaveValue value = null;// = node.getValues().get(command.getValueIndex());
//        if (value == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"getValue\",\"errorCode\":\"2\",\"errorMessage\":\"undefined value %d in node %d\" }",
//                    command.getValueIndex(),
//                    command.getNodeId()
//            ));
//            return feedback;
//        }
//
//        // установка значения параметра
//        if (Manager.get().setValueAsString(value.getValueId(), command.getValueNew())) {
//
//            // формируем триггер изменения параметра
//            ZWaveValueCallback callback = new ZWaveValueCallback() {
//                @Override
//                public void onCallback(ZWaveValue value) {
//                    feedback.setCreated(true);
//                }
//            };
//            value.addEvent(callback);
//            // ожидаем срабатывания триггера изменении параметра (~ 2сек)
//            int count = 0;
//            while (!feedback.isCreated() && count < 40) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException ex) {
//                    Thread.currentThread().interrupt();
//                }
//                count++;
//            }
//            // закрываем триггер
//            value.removeEvent(callback);
//            // Получаем новое значение параметра для ответа
//            //feedback.setFeedback(commandGetValue(command).getFeedback());
//
//        } else
//            feedback.setFeedback(String.format("{\"errorPoint\":\"setValue\",\"errorCode\":\"3\",\"errorMessage\":\"value %d in node %d not set\" }",
//                    command.getValueIndex(),
//                    command.getNodeId()
//            ));
//
//        return feedback;
//    }
//
//
//    // ======  команда - ассоциирование двух узлов сети между собой
//
//    public ZWaveFeedback commandAddAssociation(ZWaveCommand command) {
//        ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // Проверка корректности данных команды
//        if (home.getNodes().get(command.getNodeId()) == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"addAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
//                    command.getNodeId()
//            ));
//            return feedback;
//        }
//        if (home.getNodes().get(command.getTargetId()) == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"addAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined target node %d\" }",
//                    command.getTargetId()
//            ));
//            return feedback;
//        }
//        if (!getHome().getNodes().get(command.getNodeId()).existGroup(command.getGroupId())) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"addAssociation\",\"errorCode\":\"4\",\"errorMessage\":\"node %d not have a group %d\" }",
//                    command.getTargetId(),
//                    command.getGroupId()
//            ));
//            return feedback;
//        }
//
//        // процесс
//        Manager.get().addAssociation(home.getHomeId(), command.getNodeId(), command.getGroupId(), command.getTargetId());
//        feedback.setFeedback("{\"point\":\"addAssociation\",\"message\":\"ok\"}");
//
//        return feedback;
//    }
//
//
//    // ======  команда - удаление ассоциации двух узлов сети между собой
//
//    public ZWaveFeedback commandRemoveAssociation(ZWaveCommand command) {
//        ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // Проверка корректности данных команды
//        if (home.getNodes().get(command.getNodeId()) == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
//                    command.getNodeId()
//            ));
//            return feedback;
//        }
//        if (home.getNodes().get(command.getTargetId()) == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined target node %d\" }",
//                    command.getTargetId()
//            ));
//            return feedback;
//        }
//        if (!getHome().getNodes().get(command.getNodeId()).existGroup(command.getGroupId())) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"4\",\"errorMessage\":\"node %d not have a group %d\" }",
//                    command.getTargetId(),
//                    command.getGroupId()
//            ));
//            return feedback;
//        }
//
//        // процесс
//        Manager.get().removeAssociation(home.getHomeId(), command.getNodeId(), command.getGroupId(), command.getTargetId());
//        feedback.setFeedback("{\"point\":\"removeAssociation\",\"message\":\"ok\"}");
//
//        return feedback;
//    }
//
//
//    // ======  команда - получение списка ассоциированных узлов сети
//
//    public ZWaveFeedback commandGetAssociations(ZWaveCommand command) {
//        ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // Проверка корректности данных команды
//        if (home.getNodes().get(command.getNodeId()) == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"getAssociations\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
//                    command.getNodeId()
//            ));
//            return feedback;
//        }
//        if (!getHome().getNodes().get(command.getNodeId()).existGroup(command.getGroupId())) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"4\",\"errorMessage\":\"node %d not have a group %d\" }",
//                    command.getTargetId(),
//                    command.getGroupId()
//            ));
//            return feedback;
//        }
//
//        // процесс
//        AtomicReference<short[]> associations = new AtomicReference<short[]>();
//        long num = Manager.get().getAssociations(home.getHomeId(), command.getNodeId(), command.getGroupId(), associations);
//        String result = String.format("{\"nodeId\":\"%d\",\"groupId\":\"%d\",\"number\":\"%d\",\"nodes\":[",
//                command.getNodeId(),
//                command.getGroupId(),
//                num
//        );
//        for (int i = 0; i < (int) num; i++) {
//            if (i > 0) result += ",";
//            result += String.format("%d", associations.get()[i]);
//        }
//        result += "]}";
//        feedback.setFeedback(result);
//
//        return feedback;
//    }
//
//
//    // ======  команда - получение максимального количества ассоциированных узлов сети
//
//    public ZWaveFeedback commandGetMaxAssociation(ZWaveCommand command) {
//        ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // Проверка корректности данных команды
//        if (home.getNodes().get(command.getNodeId()) == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"getMaxAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
//                    command.getNodeId()
//            ));
//            return feedback;
//        }
//        if (!getHome().getNodes().get(command.getNodeId()).existGroup(command.getGroupId())) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"4\",\"errorMessage\":\"node %d not have a group %d\" }",
//                    command.getTargetId(),
//                    command.getGroupId()
//            ));
//            return feedback;
//        }
//
//        // процесс
//        short num = Manager.get().getMaxAssociations(home.getHomeId(), command.getNodeId(), command.getGroupId());
//        feedback.setFeedback(String.format("{\"nodeId\":\"%d\",\"groupId\":\"%d\",\"maxNumber\":\"%d\"}",
//                command.getNodeId(),
//                command.getGroupId(),
//                num
//        ));
//
//        return feedback;
//    }
//
//
//    // ======  команда - получение количества групп узла сети
//
//    public ZWaveFeedback commandGetNumGroups(ZWaveCommand command) {
//        ZWaveFeedback feedback = new ZWaveFeedback();
//
//        // Проверка корректности данных команды
//        if (home.getNodes().get(command.getNodeId()) == null) {
//            feedback.setFeedback(String.format("{\"errorPoint\":\"getNumGroups\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
//                    command.getNodeId()
//            ));
//            return feedback;
//        }
//
//        // процесс
//        short num = Manager.get().getNumGroups(home.getHomeId(), command.getNodeId());
//        feedback.setFeedback(String.format("{\"nodeId\":\"%d\",\"numberGroups\":\"%d\"}",
//                command.getNodeId(),
//                num
//        ));
//
//        return feedback;
//    }
}
