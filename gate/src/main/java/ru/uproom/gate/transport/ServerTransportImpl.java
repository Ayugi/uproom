package ru.uproom.gate.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.uproom.gate.commands.GateCommander;
import ru.uproom.gate.domain.DelayTimer;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.HandshakeCommand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * class with functionality of changing data with server
 * <p/>
 * Created by osipenko on 08.08.14.
 */
@Service
public class ServerTransportImpl implements ServerTransport {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(ServerTransportImpl.class);

    @Value("${cloud_host}")
    private String host;
    @Value("${cloud_port}")
    private int port;
    @Value("${connection_attempts}")
    private int times = 0;
    @Value("${period_between_attempts}")
    private long periodBetweenAttempts = 0;
    @Value("${period_connection_check}")
    private long periodCheck = 0;
    @Value("${period_connection_wait}")
    private long periodWait = 0;
    @Value("${gateId}")
    private int gateId;


    private ServerTransportReader reader;
    private Thread threadReader;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private boolean running = false;
    private boolean failed = false;

    @Autowired
    private GateCommander commander;


    //##############################################################################################################
    //######    constructors


    public ServerTransportImpl() {
    }


    //------------------------------------------------------------------------
    //  initialization / reinitialization

    @PostConstruct
    public void init() {
        reader = new ServerTransportReader();
        threadReader = new Thread(reader);
        threadReader.start();
    }


    //##############################################################################################################
    //######    getters & setters


    //------------------------------------------------------------------------
    //  is connection established ?

    public boolean isRunning() {
        return running;
    }


    //------------------------------------------------------------------------
    //  is connection failed ?

    public boolean isFailed() {
        boolean temp = failed;
        setFailed(false);
        return temp;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
        if (failed) this.running = false;
    }


    //##############################################################################################################
    //######    inner classes


    //------------------------------------------------------------------------
    //  read commands from server

    private void stop() {

        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        socket = null;
        input = null;
        output = null;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  close connection

    @PreDestroy
    public void close() {
        threadReader.interrupt();
    }


    //------------------------------------------------------------------------
    //  close connection

    public boolean getInputStream() {
        try {
            input = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }


    //------------------------------------------------------------------------
    //  create input stream

    public boolean open() {
        try {
            socket = new Socket(this.host, this.port);
            output = new ObjectOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return false;
        }
    }


    //------------------------------------------------------------------------
    //  open connections

    @Override
    public void sendCommand(Command command) {
        try {
            if (output != null) output.writeObject(command);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }


    //------------------------------------------------------------------------
    //  send command from gate to server

    public class ServerTransportReader implements Runnable {

        @Override
        public void run() {

            // set connections
            boolean work = open();
            if (work) {
                sendCommand(new HandshakeCommand(gateId));
                work = getInputStream();
            }

            // read commands
            Command command = null;
            while (work) {
                LOG.debug("Waiting for next command from server");

                // get next command
                try {
                    if (input != null)
                        command = (Command) input.readObject();
                } catch (IOException e) {
                    work = false;
                    LOG.error(e.getMessage());
                } catch (ClassNotFoundException e) {
                    work = false;
                    LOG.error(e.getMessage());
                }

                if (command != null) {
                    LOG.debug("receive command : {}", command.getType().name());
                    if (commander != null) commander.execute(command);
                } else work = false;
            }

            if (Thread.currentThread().isInterrupted()) return;
            // restart connection
            stop();
            DelayTimer.sleep(periodBetweenAttempts);
            init();
        }
    }

}
