package ru.uproom.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static String ZWAVE_DRIVER_NAME = "/dev/ttyUSB0";
    private static String ADDRESS_SERVER_NAME = "http://";


    //##############################################################################################################
    //######    entry point


    public static void main(String[] args) {

        System.out.println(">>> loading");
        // spring initialization
//        ClassPathXmlApplicationContext ctx =
//                new ClassPathXmlApplicationContext("applicationContext.xml");

        // loading openZWave library
        LOG.info("GATE STARTED");
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
        System.out.println(">>> test link");
        LOG.debug("CREATE TEST LINK OBJECT");
        ServerTransportTest serverTransportTest = new ServerTransportTest(6009);
        Thread threadServerTransportTest = new Thread(serverTransportTest);
        threadServerTransportTest.start();
        // creating link with server
        System.out.println(">>> link");
        LOG.debug("CREATING LINK WITH CLOUD SERVER...");
        ServerTransportKeeper link = new ServerTransportKeeper("localhost", 6009, 3, 500, 5000, commander);
        LOG.debug("LINK WITH CLOUD SERVER CREATED");
        // add subscriber for set/break link with server
        link.add(watcher);

        // event control loop
        System.out.println(">>> work");
        do {

            // if Z-Wave driver not ready, restart it
            if (home.isFailed()) {
                manager.removeDriver(ZWAVE_DRIVER_NAME);
                manager.addDriver(ZWAVE_DRIVER_NAME);
            }

            // Decimation signal to avoid overloading of the processor
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        } while (!watcher.isDoExit());

        // exit from program
        LOG.debug("STOPPING GATE...");

        serverTransportTest.close();
        link.close();
        manager.removeWatcher(watcher, null);
        manager.removeDriver(ZWAVE_DRIVER_NAME);
        Manager.destroy();
        Options.destroy();

        LOG.debug("GATE STOPPED");
    }
}
