package ru.uproom.gate;

import org.zwave4j.Manager;
import org.zwave4j.NativeLibraryLoader;
import org.zwave4j.Options;
import org.zwave4j.ZWave4j;

/**
 * Created by osipenko on 27.07.14.
 */
public class Main {

    private static String ZWAVE_DRIVER_NAME = "/dev/ttyUSB0";

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

        // создаем список узлов сети Z-Wave
        ZWaveHome home = new ZWaveHome();

        // добавляем обработчик событий объекта управления сетью Z-Wave
        MainWatcher watcher = new MainWatcher();
        watcher.setHome(home);
        manager.addWatcher(watcher, null);

        // добавляем обработчик команд контроллера сети Z-Wave
        MainCommander commander = new MainCommander();
        commander.setWatcher(watcher);
        commander.setHome(home);

        // активируем драйвер контроллера Z-Wave
        manager.addDriver(ZWAVE_DRIVER_NAME);

        // активируем канал связи с сервером
        CommunicationWithServer communicator = new CommunicationWithServer("localhost", 6009);
        communicator.setHome(home);
        communicator.setCommander(commander);
        communicator.setWatcher(watcher);
        Thread communicatorThread = new Thread(communicator);
        communicatorThread.start();

        // цикл исполнения команд
        do {

            // Если сеть не готова, перезапускаем драйвер
            if (watcher.isFailed()) {
                watcher.setFailed(false);
                manager.removeDriver(ZWAVE_DRIVER_NAME);
                manager.addDriver(ZWAVE_DRIVER_NAME);
            }

            // Прореживание сигналов во избежание перегрузки процессора
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        } while (!commander.isExit());

        System.out.println("---- program stopping ----");

        while (communicatorThread.isInterrupted()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        manager.removeWatcher(watcher, null);
        manager.removeDriver(ZWAVE_DRIVER_NAME);
        Manager.destroy();
        Options.destroy();

        System.out.print("---- program stopped ----");
    }

}
