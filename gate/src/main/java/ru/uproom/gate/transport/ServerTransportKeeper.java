package ru.uproom.gate.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.commands.GateCommander;
import ru.uproom.gate.domain.DelayTimer;
import ru.uproom.gate.notifications.GateWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for set, keep and break connection with server
 * <p/>
 * Created by osipenko on 06.09.14.
 */
public class ServerTransportKeeper implements Runnable, AutoCloseable {


    //##############################################################################################################
    //######    fields

    private static final Logger LOG = LoggerFactory.getLogger(ServerTransport.class);

    private int times = 0;
    private long periodTimes = 0;
    private long periodCheck = 0;
    private long periodWait = 0;
    private String host = "localhost";
    private int port = 6009;

    private boolean work = true;

    private GateCommander commander = null;
    private GateWatcher watcher = null;
    private ServerTransport transport = null;
    private Thread thread;
    private List<ServerTransportUser> transportUsers = new ArrayList<>();


    //##############################################################################################################
    //######    getters and setters


    public ServerTransportKeeper(String host,
                                 int port,
                                 int times,
                                 long periodTimes,
                                 long periodCheck,
                                 long periodWait,
                                 GateCommander commander,
                                 GateWatcher watcher
    ) {
        this.host = host;
        this.port = port;
        this.times = times;
        this.periodTimes = periodTimes;
        this.periodCheck = periodCheck;
        this.commander = commander;
        this.watcher = watcher;
    }


    //##############################################################################################################
    //######    constructors

    public List<ServerTransportUser> getTransportUsers() {
        return transportUsers;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  open connection

    private boolean checkOpen() {
        long counter = 0;
        while (!transport.isRunning() && !transport.isFailed() && (counter < periodWait || periodWait <= 0)) {
            DelayTimer.sleep(100);
            counter += 100;
        }
        return transport.isRunning();
    }


    //------------------------------------------------------------------------
    //  update listeners

    private void updateTransportUsers() {
        for (ServerTransportUser user : transportUsers) {
            user.setTransport(transport);
            user.setLink(transport.isRunning());
        }
    }


    //------------------------------------------------------------------------
    //  open connection

    private boolean open() {
        transport = new ServerTransport(host, port, commander, watcher);
        thread = new Thread(transport);
        thread.start();
        updateTransportUsers();
        return checkOpen();
    }


    //------------------------------------------------------------------------
    //  repeat open

    private boolean repeat() {
        int counter = 0;
        while (work && (counter < times || times <= 0)) {
            // open connection
            if (open()) return true;
            else stop();
            // delay for new attempt
            DelayTimer.sleep(periodTimes);
            // next attempt
            counter++;
        }
        return false;
    }


    //------------------------------------------------------------------------
    //  end of work

    @Override
    public void close() {
        work = false;
    }


    //------------------------------------------------------------------------
    //  close transport

    private void stop() {
        if (transport != null) transport.close();
        thread = null;
    }


    //------------------------------------------------------------------------
    //  run, check, close connection

    @Override
    public void run() {

        while (work) {

            // open connection
            boolean running = repeat();
            if (!running) {
                DelayTimer.sleep(periodCheck);
                continue;
            }
            // call subscribers - link set
            LOG.info("Connection with cloud server established");
            updateTransportUsers();
            // check connection
            while (running && work) {
                // slowdown
                DelayTimer.sleep(periodCheck);
                // check
                running = transport.isRunning();
            }
            // call subscribers - link break
            LOG.error("Connection with cloud server broken");
            updateTransportUsers();
            // close connection
            stop();

        }
    }

}
