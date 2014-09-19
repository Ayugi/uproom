package ru.uproom.gate;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zwave4j.Manager;
import org.zwave4j.NativeLibraryLoader;
import org.zwave4j.Options;
import org.zwave4j.ZWave4j;
import ru.uproom.gate.handlers.MainCommander;
import ru.uproom.gate.notifications.MainWatcher;
import ru.uproom.gate.test.ServerTransportTest;
import ru.uproom.gate.transport.ServerTransportKeeper;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 27.07.14.
 */
public class Main {

    private static String ZWAVE_DRIVER_NAME = "/dev/ttyUSB0";
    private static String ADDRESS_SERVER_NAME = "http://";

    public static void main(String[] args) {

        // spring initialization
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("applicationContext.xml");

        // loading openZWave library
        System.out.println("---- program started ----");
        NativeLibraryLoader.loadLibrary(ZWave4j.LIBRARY_NAME, ZWave4j.class);

        // reading current librarian options
        final Options options = Options.create(
                "/home/osipenko/.uproom21/zwave",
                "/home/osipenko/.uproom21/config",
                ""
        );
        options.addOptionBool("ConsoleOutput", false);
        options.lock();

        // creating main Z-Wave object
        Manager manager = Manager.create();

        // creating map of Z-Wave Nodes
        ZWaveHome home = new ZWaveHome();

        // add main class of Z-Wave notifications
        MainWatcher watcher = new MainWatcher();
        watcher.setHome(home);
        manager.addWatcher(watcher, null);

        // add a command handler controller Z-Wave network
        MainCommander commander = new MainCommander();
        commander.setWatcher(watcher);
        commander.setHome(home);

        // activating Z-Wave controller driver
        manager.addDriver(ZWAVE_DRIVER_NAME);

        // creating test object for server transport system
        System.out.println("Main >>>> create test link object");
        ServerTransportTest serverTransportTest = new ServerTransportTest(6009);
        Thread threadServerTransportTest = new Thread(serverTransportTest);
        threadServerTransportTest.start();
        // creating link with server
        System.out.println("Main >>>> create link object");
        ServerTransportKeeper link = new ServerTransportKeeper("localhost", 6009, 3, 500, 5000, commander);
        System.out.println("Main >>>> continue after creating");
        // add subscriber for set/break link with server
        link.add(watcher);

        // event control loop
        do {

            // channel for messages from gate to server

            // if Z-Wave driver not ready, restart it
            if (watcher.isFailed()) {
                watcher.setFailed(false);
                manager.removeDriver(ZWAVE_DRIVER_NAME);
                manager.addDriver(ZWAVE_DRIVER_NAME);
            }

            // Decimation signal to avoid overloading of the processor
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        } while (!commander.isExit());

        // exit from program
        System.out.println("---- program stopping ----");

        serverTransportTest.close();
        link.close();
        manager.removeWatcher(watcher, null);
        manager.removeDriver(ZWAVE_DRIVER_NAME);
        Manager.destroy();
        Options.destroy();

        System.out.print("---- program stopped ----");
    }

}
