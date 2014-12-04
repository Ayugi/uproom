package ru.uproom.gate.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.uproom.gate.commands.GateCommander;
import ru.uproom.gate.domain.DelayTimer;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.HandshakeCommand;
import ru.uproom.gate.transport.command.PingCommand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
        } catch (UnknownHostException e) {
            LOG.error("[UnknownHostException] - " + e.getMessage());
        } catch (IOException e) {
            LOG.error("[IOException] - " + e.getMessage());
        }
        return false;
    }


    //------------------------------------------------------------------------
    //  open connections

    @Override
    public void sendCommand(Command command) {
        try {
            if (output != null) output.writeObject(command);
            if (!(command instanceof PingCommand)) {
                if (command instanceof HandshakeCommand) {
                    LOG.debug("Done handshake with server ( Gate ID = " +
                            ((HandshakeCommand) command).getGateId() + " )");
                } else
                    LOG.debug("Send command to server : " + command.getType().name());
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }


    //------------------------------------------------------------------------
    //  restart connection from outside

    @Override
    public void restartLink() {
        LOG.error("link with server must be restarted");
        stop();
        reader.setReaderWork(false);
    }


    //------------------------------------------------------------------------
    //  send command from gate to server

    public class ServerTransportReader implements Runnable {

        private boolean isReaderWork;

        public void setReaderWork(boolean isReaderWork) {
            this.isReaderWork = isReaderWork;
        }

        @Override
        public void run() {

            // set connections
            isReaderWork = open();
            if (isReaderWork) {
                sendCommand(new HandshakeCommand(gateId));
                isReaderWork = getInputStream();
            }

            // read commands
            Command command = null;
            while (isReaderWork) {

                // get next command
                try {
                    if (input != null)
                        command = (Command) input.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    isReaderWork = false;
                    LOG.error(e.getMessage());
                }

                if (command != null) {
                    if (command.getType() != CommandType.Ping)
                        LOG.debug("receive command : {}", command.getType().name());
                    if (commander != null) commander.execute(command);
                } else isReaderWork = false;
            }

            if (Thread.currentThread().isInterrupted()) return;
            // restart connection
            stop();
            DelayTimer.sleep(periodBetweenAttempts);
            init();
        }
    }

}
