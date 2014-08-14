package ru.uproom.gate;

import org.zwave4j.*;

/**
 * Реализация класса обработки команд контроллеру сети Z-Wave
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

        // команда включения всех устройств
        if (command.getCommand().equalsIgnoreCase("switch all on")) feedback = commandSwitchAllOn(command);

            // команда отключения всех устройств
        else if (command.getCommand().equalsIgnoreCase("switch all off")) feedback = commandSwitchAllOff(command);

            // добавление новых устройств
        else if (command.getCommand().equalsIgnoreCase("add mode")) feedback = commandAddDevice(command);

            // Удаление существующих устройств
        else if (command.getCommand().equalsIgnoreCase("remove mode")) feedback = commandRemoveDevice(command);

            // прерывание исполняющейся команды
        else if (command.getCommand().equals("cancel")) feedback = commandCancel(command);

            // Получение списка узлов сети
        else if (command.getCommand().equals("get node list")) feedback = commandGetNodeList(command);

            // Получение списка параметров узла
        else if (command.getCommand().equals("get value list")) feedback = commandGetValueList(command);

            // Получение признаков параметра узла
        else if (command.getCommand().equals("get value")) feedback = commandGetValue(command);

        return feedback;

    }


    //------------------------------------------------------------------------
    //  Обработка команд сети устройств Z-Wave


    // ======  команда - Включить все узлы

    public ZWaveFeedback commandSwitchAllOn(ZWaveCommand command) {
        Manager.get().switchAllOn(watcher.getHomeId());
        ZWaveFeedback feedback = new ZWaveFeedback();
        feedback.setFeedback(command.getCommand() + " ( yes )");
        return feedback;
    }


    // ======  команда - Отключить все узлы

    public ZWaveFeedback commandSwitchAllOff(ZWaveCommand command) {
        Manager.get().switchAllOff(watcher.getHomeId());
        ZWaveFeedback feedback = new ZWaveFeedback();
        feedback.setFeedback(command.getCommand() + " ( yes )");
        return feedback;
    }


    // ======  команда - Добавить новый узел

    public ZWaveFeedback commandAddDevice(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        if (Manager.get().beginControllerCommand(watcher.getHomeId(),
                ControllerCommand.ADD_DEVICE,
                new ControllerCallback() {
                    @Override
                    public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {
                        System.out.println(String.format(
                                "Add mode\n" +
                                        "\tcontroller in state: %s" +
                                        "\tcontroller error: %s",
                                controllerState,
                                controllerError
                        ));
                    }
                })) feedback.setFeedback(String.format("%s ( yes )", command.getCommand()));
        else feedback.setFeedback(String.format("%s ( no )", command.getCommand()));

        return feedback;
    }


    // ======  команда - Добавить новый узел

    public ZWaveFeedback commandRemoveDevice(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        if (Manager.get().beginControllerCommand(watcher.getHomeId(),
                ControllerCommand.REMOVE_DEVICE,
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
                })) feedback.setFeedback(String.format("%s ( yes )", command.getCommand()));
        else feedback.setFeedback(String.format("%s ( no )", command.getCommand()));

        return feedback;
    }


    // ======  команда - Отмена текущей команды

    public ZWaveFeedback commandCancel(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        if (Manager.get().cancelControllerCommand(watcher.getHomeId()))
            feedback.setFeedback(String.format("%s ( yes )", command.getCommand()));
        else feedback.setFeedback(String.format("%s ( no )", command.getCommand()));

        return feedback;
    }


    // ======  команда - Получение списка узлов сети

    public ZWaveFeedback commandGetNodeList(ZWaveCommand command) {
        ZWaveFeedback feedback = new ZWaveFeedback();

        feedback.setFeedback(home.getNodeList());

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
            feedback.setFeedback(String.format("{ \"errorcode\":\"1\", \"message\"=\"undefined node %d\" }",
                    command.getNodeId()
            ));
            return feedback;
        }
        ZWaveValue value = node.get(command.getValueIndex());
        if (value == null) {
            feedback.setFeedback(String.format("{ \"errorcode\":\"2\", \"message\"=\"undefined value %d in node %d\" }",
                    command.getValueIndex(),
                    command.getNodeId()
            ));
            return feedback;
        }
        feedback.setFeedback(node.get(command.getValueIndex()).toString());

        return feedback;
    }

}
