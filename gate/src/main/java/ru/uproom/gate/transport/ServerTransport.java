package ru.uproom.gate.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.uproom.gate.handlers.GateCommander;
import ru.uproom.gate.notifications.GateNotificationType;
import ru.uproom.gate.notifications.GateWatcher;
import ru.uproom.gate.transport.command.Command;

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
public class ServerTransport implements ServerTransportMarker, AutoCloseable, Runnable {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(ServerTransport.class);

    private String host = "localhost";
    private int port = 6009;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private boolean running = false;

    private GateCommander commander;
    private GateWatcher watcher;


    //##############################################################################################################
    //######    constructors


    public ServerTransport() throws IOException {
        this("localhost", 6009, null, null);
    }


    public ServerTransport(String host, int port, GateCommander commander, GateWatcher watcher) {
        // save connection parameters
        this.host = host;
        this.port = port;
        this.commander = commander;
        this.watcher = watcher;
    }


    //##############################################################################################################
    //######    getters & setters


    //------------------------------------------------------------------------
    //  is connection established ?

    public boolean isRunning() {
        return running;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  close connection

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


    //------------------------------------------------------------------------
    //  open connections

    public boolean sendHandshake() {
        return watcher.onGateEvent(GateNotificationType.Handshake, null);
    }


    //------------------------------------------------------------------------
    //  create input stream

    public boolean getInputStream() {
        try {
            input = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return false;
        }
    }


    //------------------------------------------------------------------------
    //  open connections

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
    //  end of work

    @Override
    public void close() {
        running = false;
    }


    //------------------------------------------------------------------------
    //  send command from gate to server

    public boolean sendCommand(Command command) {

        try {
            output.writeObject(command);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return false;
        }

        return true;
    }


    //------------------------------------------------------------------------
    //  receive command from server to gate

    public Command receiveCommand() {

        Command command = null;
        LOG.debug("Waiting for next command from server");

        // get next command
        try {
            command = (Command) input.readObject();
        } catch (IOException e) {
            command = null;
            LOG.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            command = null;
            LOG.error(e.getMessage());
        }

        if (command != null)
            LOG.debug("receive command : {}", command.getType().name());
        return command;
    }


    //------------------------------------------------------------------------
    //  run receiving command thread

    @Override
    public void run() {

        running = open();
        if (running) {
            sendHandshake();
            running = getInputStream();
        }

        Command command = null;
        while (running) {

            // get next command
            command = receiveCommand();
            // if command not received - close connection
            if (command == null) {
                close();
                continue;
            }
            // execute command
            commander.execute(command);
        }
        stop();
    }

}
