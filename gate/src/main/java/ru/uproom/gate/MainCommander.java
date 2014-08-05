package ru.uproom.gate;

import org.zwave4j.*;

/**
 * Реализация класса обработки команд контроллеру сети Z-Wave
 * <p/>
 * Created by osipenko on 05.08.14.
 */
public class MainCommander {


    //##############################################################################################################
    //######    параметры класса


    private Manager manager = null;
    private MainWatcher watcher = null;
    private boolean exit = false;


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


    //##############################################################################################################
    //######    методы класса-


    //------------------------------------------------------------------------
    //  первичный обработчик команд

    public void execute(ZWaveCommand command) {

        // Команда завершения работы шлюза
        if (command.getCommand().equals("quit")) {
            setExit(true);
            return;
        }

        // команда включения всех устройств
        if (command.getCommand().equals("switch all on")) {
            manager.switchAllOn(watcher.getHomeId());

            // команда отключения всех устройств
        } else if (command.getCommand().equals("switch all off")) {
            manager.switchAllOff(watcher.getHomeId());

            // добавление новых устройств
        } else if (command.getCommand().equals("add mode")) {
            if (manager.beginControllerCommand(watcher.getHomeId(),
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
                    })) System.out.println("Add Mode approved");
            else System.out.println("Add Mode cancelled");

            // Удаление существующих устройств
        } else if (command.getCommand().equals("remove mode")) {
            if (manager.beginControllerCommand(watcher.getHomeId(),
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
                    })) System.out.println("Remove Mode enabled");
            else System.out.println("Remove Mode disabled");

            // прерывание исполняющейся команды
        } else if (command.getCommand().equals("cancel")) {
            if (manager.cancelControllerCommand(watcher.getHomeId())) System.out.println("Current Mode cancelled");
            else System.out.println("Current Mode stilled");
        }

    }


}
