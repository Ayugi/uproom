package ru.uproom.gate.transport;

import ru.uproom.gate.handlers.GateCommander;
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
public class ServerTransport implements ServerTransportMarker, AutoCloseable, Runnable {


    //##############################################################################################################
    //######    fields


    private String host = "localhost";
    private int port = 6009;
    private Socket socket = null;
    private ObjectInputStream input = null;
    private ObjectOutputStream output = null;

    private Thread thread = null;
    private boolean running = false;

    private GateCommander commander = null;


    //##############################################################################################################
    //######    constructors


    public ServerTransport() throws IOException {
        this("localhost", 6009, null);
    }


    public ServerTransport(String host, int port, GateCommander commander) throws IOException {
        // save connection parameters
        this.host = host;
        this.port = port;
        this.commander = commander;
        // create connection
        this.socket = new Socket(this.host, this.port);
        // get stream for commands from server
        this.input = new ObjectInputStream(socket.getInputStream());
        // get stream for messages to server
        this.output = new ObjectOutputStream(socket.getOutputStream());
        // run new thread
        thread = new Thread(this);
        thread.start();
        // we are running
        this.running = true;
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
        } catch (IOException e) {
            System.out.println("[ERR] - ServerTransport - close - " + e.getLocalizedMessage());
        }

        try {
            if (output != null) output.close();
        } catch (IOException e) {
            System.out.println("[ERR] - ServerTransport - close - " + e.getLocalizedMessage());
        }

        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("[ERR] - ServerTransport - close - " + e.getLocalizedMessage());
        }

        socket = null;
        input = null;
        output = null;
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
            System.out.println("[ERR] - RequestToServer - sendCommand - " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }


    //------------------------------------------------------------------------
    //  receive command from server to gate

    public Command receiveCommand() {

        Command command = null;
        System.out.println("[INF] - ServerTransport - receiveCommand - waiting for next command...");

        // get next command
        try {
            command = (Command) input.readObject();
        } catch (IOException e) {
            command = null;
            System.out.println("[ERR] - ServerTransport - receiveCommand - " + e.getMessage());
        } catch (ClassNotFoundException e) {
            command = null;
            System.out.println("[ERR] - ServerTransport - receiveCommand - " + e.getMessage());
        }

        if (command != null)
            System.out.println("[INF] - ServerTransport - receiveCommand - have command : " + command.getType().name());
        return command;
    }


    //------------------------------------------------------------------------
    //  run receiving command thread

    @Override
    public void run() {

        Command command = null;
        while (running) {

            // get next command
            command = receiveCommand();
            // if command not received - close connection
            if (command == null) close();
            // execute command
            commander.execute(command);
        }
        stop();
    }

}
