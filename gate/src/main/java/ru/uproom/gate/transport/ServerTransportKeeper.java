package ru.uproom.gate.transport;

import ru.uproom.gate.domain.DelayTimer;
import ru.uproom.gate.handlers.GateCommander;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for set, keep and break connection with server
 * <p/>
 * Created by osipenko on 06.09.14.
 */
public class ServerTransportKeeper
        extends ArrayList<ServerTransportUser>
        implements Runnable, AutoCloseable {


    //##############################################################################################################
    //######    fields


    private int times = 0;
    private long periodTimes = 0;
    private long periodCheck = 0;
    private String host = "localhost";
    private int port = 6009;

    private Thread thread = null;
    private boolean work = true;

    private GateCommander commander = null;
    private ServerTransport transport = null;


    //##############################################################################################################
    //######    constructors


    public ServerTransportKeeper(String host,
                                 int port,
                                 int times,
                                 long periodTimes,
                                 long periodCheck,
                                 GateCommander commander) {
        this.host = host;
        this.port = port;
        this.times = times;
        this.periodTimes = periodTimes;
        this.periodCheck = periodCheck;
        this.commander = commander;
        // start thread of keeping
        thread = new Thread(this);
        thread.start();
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  open connection

    private boolean open() {
        try {
            transport = new ServerTransport(host, port, commander);
            System.out.println("ServerTransportKeeper >>>> transport object created");
            return true;
        } catch (IOException e) {
            System.out.println("[ERR] - ServerTransportKeeper - open - " + e.getLocalizedMessage());
        }
        return false;
    }


    //------------------------------------------------------------------------
    //  repeat open

    private boolean repeat() {
        int counter = 0;
        while (work && (counter < times || times <= 0)) {
            // open connection
            if (open()) return true;
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
            for (ServerTransportUser user : this) {
                user.setTransport(transport);
            }
            // check connection
            while (running && work) {
                // slowdown
                DelayTimer.sleep(periodCheck);
                // check
                running = transport.isRunning();
            }
            // call subscribers - link break
            for (ServerTransportUser user : this) {
                user.setTransport(null);
            }
            // close connection
            stop();

        }
    }

}
