package ru.uproom.gate.localinterface.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.localinterface.commands.GateLocalCommander;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.HandshakeCommand;
import ru.uproom.gate.transport.command.PingCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by osipenko on 28.12.14.
 */
public class GateLocalSocketHandler implements Runnable {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(GateLocalSocketHandler.class);

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private int userId;
    private boolean stopped;

    private GateLocalTransport transport;

    private long lastPingInterval = -1;
    private long lastPingIssued = -1;
    private ConnectionChecker checker = new ConnectionChecker();

    private GateLocalCommander commander;


    //##############################################################################################################
    //######    constructors


    public GateLocalSocketHandler(Socket socket, GateLocalTransport transport, GateLocalCommander commander) {

        this.socket = socket;
        this.transport = transport;
        this.commander = commander;

        prepareReaderStream();
        prepareWriterStream();

    }


    //##############################################################################################################
    //######    getters and setters


    public int getUserId() {
        return userId;
    }


    //##############################################################################################################
    //######    inner classes


    //------------------------------------------------------------------------
    //  checking connection

    private void prepareReaderStream() {
        try {
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  read / write streams

    private void prepareWriterStream() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(Command command) {
        if (!(command instanceof PingCommand))
            LOG.info("send command to gate : " + command.getType());
        try {
            output.writeObject(command);
        } catch (IOException e) {
            LOG.error("network failure : {}", e.getMessage());
            checker.stop();
            transport.onConnectionFailure(this);
            stop();
        }
    }


    //------------------------------------------------------------------------
    //  send command to gate

    public int handshake() {
        LOG.info("handle handshake process...");
        try {
            Object handshakeObj = input.readObject();
            if (!(handshakeObj instanceof HandshakeCommand)) {
                LOG.debug("Invalid handshake received {}", handshakeObj);
                return -1;
            }
            HandshakeCommand handshake = (HandshakeCommand) handshakeObj;
            userId = handshake.getGateId();
            LOG.info("handshake successful : userId = {}", userId);
            new Thread(checker).start();
            return userId;
        } catch (IOException e) {
            throw new RuntimeException("Failed to receive handshake ", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to receive handshake ", e);
        }
    }


    //------------------------------------------------------------------------
    //  handle for handshake process with gate

    public void stop() {
        stopped = true;
    }

    @Override
    public void run() {

        while (!stopped) {
            try {
                Command command = (Command) input.readObject();
                if (command == null) {
                    stopped = true;
                    continue;
                }
                if (command instanceof PingCommand) {
                    PingCommand ping = (PingCommand) command;
                    lastPingInterval = System.currentTimeMillis() - ping.getIssued();
                    lastPingIssued = -1;
                } else {
                    LOG.info("have command : {}", command.getType());
                    commander.execute(command);
                }

            } catch (IOException e) {
                e.printStackTrace();
                stopped = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private class ConnectionChecker implements Runnable {

        private boolean stopped;

        @Override
        public void run() {
            while (!stopped) {
                synchronized (this) {
                    try {
                        wait(5000);
                    } catch (InterruptedException e) {
                        LOG.error("unexpected interruption {}", e.getMessage());
                    }
                }
                sendCommand(new PingCommand());
                lastPingIssued = System.currentTimeMillis();
            }
        }

        public void stop() {
            stopped = true;
        }
    }
}
