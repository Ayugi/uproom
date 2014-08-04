package ru.uproom.gate;

import org.zwave4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by osipenko on 27.07.14.
 */
public class Main {

    public static void main(String[] args) {

        // загрузка библиотеки openZWave
        System.out.println("---- program started ----");
        NativeLibraryLoader.loadLibrary(ZWave4j.LIBRARY_NAME, ZWave4j.class);

        // загрузка текущих опций библиотеки
        final Options options = Options.create("/home/osipenko/.uproom21/zwave", "/home/osipenko/.uproom21/config", "");
        options.addOptionBool("ConsoleOutput", false);
        options.lock();

        // создаем объект управления сетью Z-Wave
        Manager manager = Manager.create();

        // добавляем обработчик событий объекта управления сетью Z-Wave
        MainWatcher watcher = new MainWatcher();
        watcher.setManager(manager);
        manager.addWatcher(watcher, null);

        // активируем драйвер контроллера Z-Wave
        manager.addDriver("/dev/ttyUSB3");

        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String line = null;
        do {
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!watcher.getReady() || line == null) {
                continue;
            }

            if (line.equals("on")) {
                manager.switchAllOn(watcher.getHomeId());

            } else if (line.equals("off")) {
                manager.switchAllOff(watcher.getHomeId());

                // добавление существующих устройств
            } else if (line.equals("add mode")) {
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
            } else if (line.equals("remove mode")) {
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
            } else if (line.equals("cancel")) {
                if (manager.cancelControllerCommand(watcher.getHomeId())) System.out.println("Current Mode cancelled");
                else System.out.println("Current Mode stilled");
            }
        } while (line != null && !line.equals("q"));


        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        manager.removeWatcher(watcher, null);
        manager.removeDriver("/dev/ttyUSB0");
        Manager.destroy();
        Options.destroy();

        System.out.print("---- program stopped ----");
    }

}
