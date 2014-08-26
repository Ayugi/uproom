package ru.uproom.gate;

import org.zwave4j.*;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация класса обработки команд контроллеру сети Z-Wave
 *
 * todo : диагностика наличия контроллера
 * todo : подписка на события в ватчере, нопремер таймаут
 *
 * Created by osipenko on 05.08.14.
 */
public class MainCommander {


    //##############################################################################################################
    //######    параметры класса


    private MainWatcher watcher = null;
    private boolean exit = false;
    private ZWaveHome home = null;


    //##############################################################################################################
    //######    обработка параметров класса


    //------------------------------------------------------------------------
    //  обработчик событий сети Z-Wave

    public boolean setWatcher(MainWatcher _watcher) {
        watcher = _watcher;
        return _watcher != null;
    }

    public MainWatcher getWatcher() {
        return watcher;
    }


    //------------------------------------------------------------------------
    //  признак завершения

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }


    //------------------------------------------------------------------------
    //  список узлов сети Z-Wave

    public ZWaveHome getHome() {
        return home;
    }

    public void setHome(ZWaveHome home) {
        this.home = home;
    }




    //##############################################################################################################
    //######    методы класса-


    //------------------------------------------------------------------------
    //  первичный обработчик команд

    public ZWaveFeedback execute(ZWaveCommand command) {

        ZWaveFeedback feedback = null;

        // Команда завершения работы шлюза
        if (command.getCommand().equalsIgnoreCase("quit")) {
            setExit(true);
            feedback = new ZWaveFeedback();
            feedback.setFeedback("quit ( ок )");
            return feedback;
        }

        // получение статистики драйвера ( get driver statistics )
        if (command.getCommand().equalsIgnoreCase("get driver statistics"))
            feedback = commandGetDriverStatistics(command);
            // перезапуск драйвера ( restart controller )
        else if (command.getCommand().equalsIgnoreCase("restart controller"))
            feedback = commandRestartController(command);

            // команда включения всех устройств ( switch all on )
        else if (command.getCommand().equalsIgnoreCase("switch all on")) feedback = commandSwitchAllOn(command);
            // команда отключения всех устройств ( switch all off )
        else if (command.getCommand().equalsIgnoreCase("switch all off")) feedback = commandSwitchAllOff(command);

            // добавление новых устройств ( add mode )
        else if (command.getCommand().equalsIgnoreCase("add mode")) feedback = commandAddNode(command);
            // Удаление существующих устройств ( remove mode )
        else if (command.getCommand().equalsIgnoreCase("remove mode")) feedback = commandRemoveNode(command);
            // удаление несуществующих устройств ( remove failed node )
        else if (command.getCommand().equalsIgnoreCase("remove failed node"))
            feedback = commandRemoveFailedNode(command);
            // проверка состояния сети ( test network )
        else if (command.getCommand().equalsIgnoreCase("test network")) feedback = commandTestNetwork(command);
            // прерывание исполняющейся команды ( cancel )
        else if (command.getCommand().equals("cancel")) feedback = commandCancel(command);

            // Получение идентификатора контроллера сети ( get controller node )
        else if (command.getCommand().equals("get controller node")) feedback = commandControllerNodeId(command);
            // Получение списка узлов сети ( get node list )
        else if (command.getCommand().equals("get node list")) feedback = commandGetNodeList(command);
            // Получение информации об узле сети ( get node, nodeId=# )
        else if (command.getCommand().equals("get node")) feedback = commandGetNode(command);
            // Проверка неисправного состояния узла сети ( is node failed, nodeId=# )
        else if (command.getCommand().equals("is node failed")) feedback = commandIsNodeFailed(command);
            // Обновление информации об узле сети ( refresh node, nodeId=# )
        else if (command.getCommand().equals("refresh node")) feedback = commandRefreshNodeInfo(command);
            // Запрос информации о состоянии узла сети ( node state, nodeId=# )
        else if (command.getCommand().equals("node state")) feedback = commandRequestNodeState(command);
            // Включение узла сети ( set node on, nodeId=# )
        else if (command.getCommand().equals("set node on")) feedback = commandSetNodeOn(command);
            // Включение узла сети ( set node off, nodeId=# )
        else if (command.getCommand().equals("set node off")) feedback = commandSetNodeOff(command);
            // Включение узла сети ( set node level, nodeId=#, level=# )
        else if (command.getCommand().equals("set node level")) feedback = commandSetNodeLevel(command);

            // Получение списка параметров узла ( get value list, nodeId=# )
        else if (command.getCommand().equals("get value list")) feedback = commandGetValueList(command);
            // Получение значения параметра узла ( get value, nodeId=#, valueId=# )
        else if (command.getCommand().equals("get value")) feedback = commandGetValue(command);
            // Установка значения параметра узла ( set value, nodeId=#, valueId=#, value=# )
        else if (command.getCommand().equals("set value")) feedback = commandSetValue(command);

            // ассоциирование двух узлов между собой ( add association, nodeId=#, targetId=#, groupId=#)
        else if (command.getCommand().equals("add association")) feedback = commandAddAssociation(command);
            // удаление ассоциации двух узлов между собой ( remove association, nodeId=#, targetId=#, groupId=# )
        else if (command.getCommand().equals("remove association")) feedback = commandRemoveAssociation(command);
            // список ассоциаций узла ( get associations, nodeId=#, groupId=# )
        else if (command.getCommand().equals("get associations")) feedback = commandGetAssociations(command);
            // ассоциирование двух узлов между собой ( get max associations, nodeId=#, groupId=# )
        else if (command.getCommand().equals("get max associations")) feedback = commandGetMaxAssociation(command);
            // количество групп узла ( get groups number, nodeId=# )
        else if (command.getCommand().equals("get groups number")) feedback = commandGetNumGroups(command);


        return feedback;

    }


    //------------------------------------------------------------------------
    //  Обработка команд сети устройств Z-Wave


    // ======  команда - Получить статистику драйвера

    public ZWaveFeedback commandGetDriverStatistics(ZWaveCommand command) {

        DriverData statistics = new DriverData();
        Manager.get().getDriverStatistics(getHome().getHomeId(), statistics);
        ZWaveFeedback feedback = new ZWaveFeedback();
        feedback.setFeedback("{\"point\":\"getDriverStatistics\",\"message\":\"ok\"}");

        return feedback;
    }


    // ======  команда - Перезапуск драйвера

    public ZWaveFeedback commandRestartController(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        getWatcher().setFailed(true);
        feedback.setFeedback("{\"point\":\"restartController\",\"message\":\"ok\"}");

        return feedback;
    }


    // ======  команда - Включить все узлы

    public ZWaveFeedback commandSwitchAllOn(ZWaveCommand command) {
        Manager.get().switchAllOn(watcher.getHome().getHomeId());
        ZWaveFeedback feedback = new ZWaveFeedback();
        feedback.setFeedback("{\"point\":\"switchAllOn\",\"message\":\"ok\"}");
        return feedback;
    }


    // ======  команда - Отключить все узлы

    public ZWaveFeedback commandSwitchAllOff(ZWaveCommand command) {
        Manager.get().switchAllOff(watcher.getHome().getHomeId());
        ZWaveFeedback feedback = new ZWaveFeedback();
        feedback.setFeedback("{\"point\":\"switchAllOff\",\"message\":\"ok\"}");
        return feedback;
    }


    // ======  команда - Добавить новый узел

    public ZWaveFeedback commandAddNode(ZWaveCommand command) {
        final ZWaveFeedback feedback = new ZWaveFeedback();

        // создаем триггер для события
        ControllerCallback callback = new ControllerCallback() {
            @Override
            public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {

                if (controllerState == ControllerState.WAITING && controllerError == ControllerError.NONE)
                    feedback.setCreated(true);
                System.out.println(String.format(
                        "Add mode\n" +
                                "\tcontroller in state: %s" +
                                "\tcontroller error: %s",
                        controllerState,
                        controllerError
                ));
            }
        };

        if (Manager.get().beginControllerCommand(watcher.getHome().getHomeId(), ControllerCommand.ADD_DEVICE, callback)) {

            // ожидаем срабатывания триггера (~ 2сек)
            int count = 0;
            while (!feedback.isCreated() && count < 40) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                count++;
            }
            // Получаем новое значение параметра для ответа
            if (feedback.isCreated())
                feedback.setFeedback("{\"point\":\"addMode\",\"message\":\"ok\"}");
            else
                feedback.setFeedback("{\"errorPoint\":\"addMode\",\"errorCode\":\"99\",\"errorMessage\":\"set mode timeout\" }");

        } else
            feedback.setFeedback("{\"errorPoint\":\"addMode\",\"errorCode\":\"98\",\"errorMessage\":\"mode not set\" }");

        return feedback;
    }


    // ======  команда - удалить существующий узел

    public ZWaveFeedback commandRemoveNode(ZWaveCommand command) {
        final ZWaveFeedback feedback = new ZWaveFeedback();

        // создаем триггер для события
        ControllerCallback callback = new ControllerCallback() {
            @Override
            public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {

                if (controllerState == ControllerState.WAITING && controllerError == ControllerError.NONE)
                    feedback.setCreated(true);
                System.out.println(String.format(
                        "remove mode\n" +
                                "\tcontroller in state: %s" +
                                "\tcontroller error: %s",
                        controllerState,
                        controllerError
                ));
            }
        };

        if (Manager.get().beginControllerCommand(watcher.getHome().getHomeId(), ControllerCommand.REMOVE_DEVICE, callback)) {

            // ожидаем срабатывания триггера (~ 2сек)
            int count = 0;
            while (!feedback.isCreated() && count < 40) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                count++;
            }
            // Получаем новое значение параметра для ответа
            if (count < 40)
                feedback.setFeedback("{\"errorPoint\":\"removeMode\",\"errorCode\":\"99\",\"errorMessage\":\"set mode timeout\" }");
            else
                feedback.setFeedback("{\"point\":\"removeMode\",\"message\":\"ok\"}");

        } else
            feedback.setFeedback("{\"errorPoint\":\"removeMode\",\"errorCode\":\"98\",\"errorMessage\":\"mode not set\" }");

        return feedback;
    }


    // ======  команда - удалить неисправный узел

    public ZWaveFeedback commandRemoveFailedNode(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        if (Manager.get().beginControllerCommand(watcher.getHome().getHomeId(),
                ControllerCommand.REMOVE_FAILED_NODE,
                new ControllerCallback() {
                    @Override
                    public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {
                        System.out.println(String.format(
                                "Remove mode\n" +
                                        "\tcontroller in state: %s" +
                                        "\tcontroller error: %s",
                                controllerState,
                                controllerError
                        ));
                    }
                })) feedback.setFeedback("{\"point\":\"removeFailedNode\",\"message\":\"ok\"}");

        else
            feedback.setFeedback("{\"errorPoint\":\"removeFailedNode\",\"errorCode\":\"98\",\"errorMessage\":\"remove not accepted\" }");

        return feedback;
    }


    // ======  команда - проверка состояния сети

    public ZWaveFeedback commandTestNetwork(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        Manager.get().testNetwork(home.getHomeId(), 1);
        feedback.setFeedback("{\"point\":\"testNetwork\",\"message\":\"ok\"}");

        return feedback;
    }


    // ======  команда - Отмена текущей команды

    public ZWaveFeedback commandCancel(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        if (Manager.get().cancelControllerCommand(watcher.getHome().getHomeId()))
            feedback.setFeedback("{\"point\":\"cancel\",\"message\":\"ok\"}");
        else
            feedback.setFeedback("{\"errorPoint\":\"cancel\",\"errorCode\":\"98\",\"errorMessage\":\"cancel not accepted\" }");

        return feedback;
    }


    // ======  команда - Получение списка узлов сети

    public ZWaveFeedback commandControllerNodeId(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        feedback.setFeedback(String.format("{\"controller\":\"%d\"}", Manager.get().getControllerNodeId(getHome().getHomeId())));

        return feedback;
    }


    // ======  команда - Получение списка узлов сети

    public ZWaveFeedback commandGetNodeList(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        feedback.setFeedback(home.getNodeList());

        return feedback;
    }


    // ======  команда - получение информации о конкретном узле сети

    public ZWaveFeedback commandGetNode(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getNode\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }

        // процесс
        feedback.setFeedback(node.getNodeInfo());

        return feedback;
    }


    // ======  команда - проверка недостоверности узла

    public ZWaveFeedback commandIsNodeFailed(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getNode\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }

        // процесс
        Boolean result = Manager.get().isNodeFailed(getHome().getHomeId(), node.getNodeId());
        feedback.setFeedback(String.format("{\"node\":\"%d\",\"failed\":\"%s\"}", node.getNodeId(), result.toString()));

        return feedback;
    }


    // ======  команда - обновление информации об узле

    public ZWaveFeedback commandRefreshNodeInfo(ZWaveCommand command) {
        final ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getNode\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }

        Boolean result = Manager.get().isNodeFailed(getHome().getHomeId(), node.getNodeId());
        if (Manager.get().refreshNodeInfo(getHome().getHomeId(), node.getNodeId()))
            feedback.setFeedback(String.format("{\"point\":\"refreshNodeInfo\",\"message\":\"ok\"}"));
        else
            feedback.setFeedback(String.format("{\"errorPoint\":\"getNode\",\"errorCode\":\"97\",\"errorMessage\":\"info about node %d not refreshed\" }",
                    command.getNodeId()
            ));

        return feedback;
    }


    // ======  команда - Запрос состояния узла сети

    public ZWaveFeedback commandRequestNodeState(ZWaveCommand command) {
        final ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"requestNodeState\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }


        // формирование реакции на событие
        ZWaveNodeCallback callback = new ZWaveNodeCallback() {
            @Override
            public void onCallback(ZWaveNode node, Notification notification) {
                // Если в процессе возник таймаут (1 == TIMEOUT)
                if (notification.getType() == NotificationType.NOTIFICATION && notification.getNotification() == 1) {
                    feedback.setFeedback(String.format("{\"errorPoint\":\"requestNodeState\",\"errorCode\":\"4\",\"errorMessage\":\"node %d disconnected\"}",
                            node.getNodeId()
                    ));
                    feedback.setSuccessful(false);
                }
                // Если возникло событие NODE_QUERIES_COMPLETE
                else if (notification.getType() == NotificationType.NODE_QUERIES_COMPLETE) {
                    feedback.setFeedback(String.format("{\"point\":\"requestNodeState\",\"message\":\"node %d request complete\"}",
                            node.getNodeId()
                    ));
                    feedback.setSuccessful(true);
                }
                // Необрабатываемое событие
                else {
                    feedback.setFeedback(String.format("{\"point\":\"requestNodeState\",\"message\":\"node %d send notification %s\"}",
                            node.getNodeId(),
                            notification.getType().toString()
                    ));
                }
                // ответ получен
                feedback.setCreated(true);
            }
        };
        node.addEvent(callback);

        // запрос состояния удачен
        if (Manager.get().requestNodeState(getHome().getHomeId(), node.getNodeId())) {
            // ожидаем срабатывания триггера изменении параметра (~ 2сек)
            int count = 0;
            while (!feedback.isCreated() && count < 40) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                count++;
            }
            // В случае, если проверяемым узлом был контроллер сети, перезапускаем его
            if (!feedback.isSuccessful() && Manager.get().getControllerNodeId(getHome().getHomeId()) == node.getNodeId())
                getWatcher().setFailed(true);
        }
        // Запрос состояния неудачен
        else {
            feedback.setFeedback(String.format("{\"errorPoint\":\"requestNodeState\",\"errorCode\":\"97\"\"errorMessage\":\"node %d not requested\"}",
                    node.getNodeId()
            ));
        }
        // закрываем триггер
        node.removeEvent(callback);

        return feedback;
    }


    // ======  команда - включить устройство

    public ZWaveFeedback commandSetNodeOn(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"setNodeOn\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }

        // процесс
        Manager.get().setNodeOn(home.getHomeId(), command.getNodeId());
        feedback.setFeedback(String.format("{\"point\":\"setNodeOn\",\"message\":\"node %d is ON\"}", command.getNodeId()));

        return feedback;
    }


    // ======  команда - отключить устройство

    public ZWaveFeedback commandSetNodeOff(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"setNodeOff\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }

        // процесс
        Manager.get().setNodeOff(home.getHomeId(), command.getNodeId());
        feedback.setFeedback(String.format("{\"point\":\"setNodeOff\",\"message\":\"node %d is OFF\"}", command.getNodeId()));

        return feedback;
    }


    // ======  команда - установить уровень узла

    public ZWaveFeedback commandSetNodeLevel(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"setNodeLevel\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }

        // процесс
        Manager.get().setNodeLevel(home.getHomeId(), command.getNodeId(), command.getLevel());
        feedback.setFeedback(String.format("{\"point\":\"setNodeLevel\",\"message\":\"node %d set level %d\"}",
                command.getNodeId(),
                command.getLevel()
        ));

        return feedback;
    }


    // ======  команда - Получение списка параметров узла

    public ZWaveFeedback commandGetValueList(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        feedback.setFeedback(home.get(command.getNodeId()).getValueList());

        return feedback;
    }


    // ======  команда - Получение значения параметра узла

    public ZWaveFeedback commandGetValue(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getValue\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }
        ZWaveValue value = node.get(command.getValueIndex());
        if (value == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getValue\",\"errorCode\":\"2\",\"errorMessage\":\"undefined value %d in node %d\" }",
                    command.getValueIndex(),
                    command.getNodeId()
            ));
            return feedback;
        }
        feedback.setFeedback(node.get(command.getValueIndex()).toString());

        return feedback;
    }


    // ======  команда - Установка значения параметра узла

    public ZWaveFeedback commandSetValue(final ZWaveCommand command) {
        final ZWaveFeedback feedback = new ZWaveFeedback();

        // проверка корректности параметров команды
        ZWaveNode node = home.get(command.getNodeId());
        if (node == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"setValue\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }
        ZWaveValue value = node.get(command.getValueIndex());
        if (value == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getValue\",\"errorCode\":\"2\",\"errorMessage\":\"undefined value %d in node %d\" }",
                    command.getValueIndex(),
                    command.getNodeId()
            ));
            return feedback;
        }

        // установка значения параметра
        if (Manager.get().setValueAsString(value.getValueId(), command.getValueNew())) {

            // формируем триггер изменения параметра
            ZWaveValueCallback callback = new ZWaveValueCallback() {
                @Override
                public void onCallback(ZWaveValue value) {
                    feedback.setCreated(true);
                }
            };
            value.addEvent(callback);
            // ожидаем срабатывания триггера изменении параметра (~ 2сек)
            int count = 0;
            while (!feedback.isCreated() && count < 40) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                count++;
            }
            // закрываем триггер
            value.removeEvent(callback);
            // Получаем новое значение параметра для ответа
            feedback.setFeedback(commandGetValue(command).getFeedback());

        } else
            feedback.setFeedback(String.format("{\"errorPoint\":\"setValue\",\"errorCode\":\"3\",\"errorMessage\":\"value %d in node %d not set\" }",
                    command.getValueIndex(),
                    command.getNodeId()
            ));

        return feedback;
    }


    // ======  команда - ассоциирование двух узлов сети между собой

    public ZWaveFeedback commandAddAssociation(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        if (home.get(command.getNodeId()) == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"addAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }
        if (home.get(command.getTargetId()) == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"addAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined target node %d\" }",
                    command.getTargetId()
            ));
            return feedback;
        }
        if (!getHome().get(command.getNodeId()).existGroup(command.getGroupId())) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"addAssociation\",\"errorCode\":\"4\",\"errorMessage\":\"node %d not have a group %d\" }",
                    command.getTargetId(),
                    command.getGroupId()
            ));
            return feedback;
        }

        // процесс
        Manager.get().addAssociation(home.getHomeId(), command.getNodeId(), command.getGroupId(), command.getTargetId());
        feedback.setFeedback("{\"point\":\"addAssociation\",\"message\":\"ok\"}");

        return feedback;
    }


    // ======  команда - удаление ассоциации двух узлов сети между собой

    public ZWaveFeedback commandRemoveAssociation(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        if (home.get(command.getNodeId()) == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }
        if (home.get(command.getTargetId()) == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined target node %d\" }",
                    command.getTargetId()
            ));
            return feedback;
        }
        if (!getHome().get(command.getNodeId()).existGroup(command.getGroupId())) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"4\",\"errorMessage\":\"node %d not have a group %d\" }",
                    command.getTargetId(),
                    command.getGroupId()
            ));
            return feedback;
        }

        // процесс
        Manager.get().removeAssociation(home.getHomeId(), command.getNodeId(), command.getGroupId(), command.getTargetId());
        feedback.setFeedback("{\"point\":\"removeAssociation\",\"message\":\"ok\"}");

        return feedback;
    }


    // ======  команда - получение списка ассоциированных узлов сети

    public ZWaveFeedback commandGetAssociations(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        if (home.get(command.getNodeId()) == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getAssociations\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }
        if (!getHome().get(command.getNodeId()).existGroup(command.getGroupId())) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"4\",\"errorMessage\":\"node %d not have a group %d\" }",
                    command.getTargetId(),
                    command.getGroupId()
            ));
            return feedback;
        }

        // процесс
        AtomicReference<short[]> associations = new AtomicReference<short[]>();
        long num = Manager.get().getAssociations(home.getHomeId(), command.getNodeId(), command.getGroupId(), associations);
        String result = String.format("{\"nodeId\":\"%d\",\"groupId\":\"%d\",\"number\":\"%d\",\"nodes\":[",
                command.getNodeId(),
                command.getGroupId(),
                num
        );
        for (int i = 0; i < (int) num; i++) {
            if (i > 0) result += ",";
            result += String.format("%d", associations.get()[i]);
        }
        result += "]}";
        feedback.setFeedback(result);

        return feedback;
    }


    // ======  команда - получение максимального количества ассоциированных узлов сети

    public ZWaveFeedback commandGetMaxAssociation(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        if (home.get(command.getNodeId()) == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getMaxAssociation\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }
        if (!getHome().get(command.getNodeId()).existGroup(command.getGroupId())) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"removeAssociation\",\"errorCode\":\"4\",\"errorMessage\":\"node %d not have a group %d\" }",
                    command.getTargetId(),
                    command.getGroupId()
            ));
            return feedback;
        }

        // процесс
        short num = Manager.get().getMaxAssociations(home.getHomeId(), command.getNodeId(), command.getGroupId());
        feedback.setFeedback(String.format("{\"nodeId\":\"%d\",\"groupId\":\"%d\",\"maxNumber\":\"%d\"}",
                command.getNodeId(),
                command.getGroupId(),
                num
        ));

        return feedback;
    }


    // ======  команда - получение количества групп узла сети

    public ZWaveFeedback commandGetNumGroups(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        // Проверка корректности данных команды
        if (home.get(command.getNodeId()) == null) {
            feedback.setFeedback(String.format("{\"errorPoint\":\"getNumGroups\",\"errorCode\":\"1\",\"errorMessage\":\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }

        // процесс
        short num = Manager.get().getNumGroups(home.getHomeId(), command.getNodeId());
        feedback.setFeedback(String.format("{\"nodeId\":\"%d\",\"numberGroups\":\"%d\"}",
                command.getNodeId(),
                num
        ));

        return feedback;
    }

}
