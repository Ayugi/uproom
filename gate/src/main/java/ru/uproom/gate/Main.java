package ru.uproom.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Manager;
import org.zwave4j.NativeLibraryLoader;
import org.zwave4j.Options;
import org.zwave4j.ZWave4j;
import ru.uproom.gate.handlers.MainCommander;
import ru.uproom.gate.notifications.MainWatcher;
import ru.uproom.gate.transport.ServerTransportKeeper;
import ru.uproom.gate.zwave.ZWaveHome;

/**
 * Created by osipenko on 27.07.14.
 */
public class Main {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String ZWAVE_DRIVER_NAME = "/dev/ttyUSB0";
    private static final String ADDRESS_SERVER_NAME = "54.191.89.147";
    private static final int ADDRESS_SERVER_PORT = 8282;


    //##############################################################################################################
    //######    entry point


    public static void main(String[] args) {

        LOG.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");
        LOG.info(" >>>> GATE STARTING ... ");
        // spring initialization
//        ClassPathXmlApplicationContext ctx =
//                new ClassPathXmlApplicationContext("applicationContext.xml");

        // loading openZWave library
        LOG.info(" >>>> Load libraries ...");
        NativeLibraryLoader.loadLibrary(ZWave4j.LIBRARY_NAME, ZWave4j.class);
        LOG.info(" >>>> Libraries loaded.");

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
//        System.out.println(">>> test link");
//        LOG.debug("CREATE TEST LINK OBJECT");
//        ServerTransportTest serverTransportTest = new ServerTransportTest(6009);
//        Thread threadServerTransportTest = new Thread(serverTransportTest);
//        threadServerTransportTest.start();
        // creating link with server
        System.out.println(">>> link");
        LOG.debug("CREATING LINK WITH CLOUD SERVER...");
        ServerTransportKeeper link = new ServerTransportKeeper(
                ADDRESS_SERVER_NAME, ADDRESS_SERVER_PORT, 3, 500, 5000, commander);
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

        //serverTransportTest.close();
        link.close();
        manager.removeWatcher(watcher, null);
        manager.removeDriver(ZWAVE_DRIVER_NAME);
        Manager.destroy();
        Options.destroy();

        LOG.debug("GATE STOPPED");
    }
}
