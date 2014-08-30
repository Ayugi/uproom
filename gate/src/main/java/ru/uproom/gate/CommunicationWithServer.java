package ru.uproom.gate;

import ru.uproom.gate.transport.Command;
import ru.uproom.gate.transport.CommandType;
import ru.uproom.gate.transport.HandshakeCommand;

import java.io.*;
import java.net.Socket;

/**
 * class with functionality of changing data with server
 *
 * Created by osipenko on 08.08.14.
 */
public class CommunicationWithServer implements AutoCloseable, Runnable {


    //##############################################################################################################
    //######    parameters


    private String host = "localhost";
    private int port = 6009;
    private Socket socket = null;
    private ObjectInputStream input = null;
    private ObjectOutputStream output = null;
    private boolean connected = false;
    private boolean reconnect = false;
    private int times = 0;

    private MainCommander commander = null;
    private MainWatcher watcher = null;
    private ZWaveHome home = null;

    private String gateId = "";


    //##############################################################################################################
    //######    constructors


    public CommunicationWithServer() {

    }


    public CommunicationWithServer(String host, int port) {
        this.host = host;
        this.port = port;
    }


    //##############################################################################################################
    //######    processing class parameters


    //------------------------------------------------------------------------
    //  host address

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    //------------------------------------------------------------------------
    //  host port

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    //------------------------------------------------------------------------
    //  object processing of external command

    public MainCommander getCommander() {
        return commander;
    }

    public void setCommander(MainCommander commander) {
        this.commander = commander;
    }


    //------------------------------------------------------------------------
    //  object processing of Z-Wave events

    public MainWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(MainWatcher watcher) {
        this.watcher = watcher;
    }


    //------------------------------------------------------------------------
    //  map of Z-Wave Nodes

    public ZWaveHome getHome() {
        return home;
    }

    public void setHome(ZWaveHome home) {
        this.home = home;
    }


    //------------------------------------------------------------------------
    //  sign communications with server

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


    //------------------------------------------------------------------------
    //  reconnect required

    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }


    //------------------------------------------------------------------------
    //  number of reconnects

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


    //------------------------------------------------------------------------
    //  number of reconnects

    public String getGateId() {
        return gateId;
    }

    public void setGateId(String gateId) {
        this.gateId = gateId;
    }


    //------------------------------------------------------------------------
    //  internal things

    protected Socket getSocket() {
        return socket;
    }

    protected ObjectInputStream getInput() {
        return input;
    }

    protected ObjectOutputStream getOutput() {
        return output;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  open link with external system

    private void createConnect() {
        try {
            // creating socket
            socket = new Socket(host, port);
            // stream reading from socket
            input = new ObjectInputStream(socket.getInputStream());
            // stream writing to socket
            output = new ObjectOutputStream(socket.getOutputStream());
            // connect established
            setConnected(true);
        } catch (IOException e) {
            setConnected(false);
            System.out.println("[ERR] - CommunicationWithServer - open - " + e.getLocalizedMessage());
        }
    }


    //------------------------------------------------------------------------
    //  open link with external system (repeatedly)

    public void open() {

        int counter = 0;
        while (!commander.isExit() && !isConnected() && (counter < getTimes() || getTimes() <= 0)) {
            // create link
            createConnect();
            // check link
            checkLink();
            // следующая попытка
            counter++;
        }

    }


    //------------------------------------------------------------------------
    //  check link and close it if not work

    public void checkLink() {
        // check link
        if (!isConnected()) {
            // close current link
            close();
            // wait time after communication gap
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }


    //------------------------------------------------------------------------
    //  close connection

    @Override
    public void close() {

        try {
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            System.out.println("[ERR] - CommunicationWithServer - close - " + e.getLocalizedMessage());
        }

        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("[ERR] - CommunicationWithServer - close - " + e.getLocalizedMessage());
        }

        socket = null;
        input = null;
        output = null;
        setConnected(false);

    }


    //------------------------------------------------------------------------
    //  send command from gate to server

    public boolean sendCommand(Command command) {

        try {
            getOutput().writeObject(command);
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
        System.out.println("[INF] - RequestToServer - receiveCommand - waiting for next command...");

        // get next command
        try {
            command = (Command) getInput().readObject();
        } catch (IOException e) {
            command = null;
            System.out.println("[ERR] - RequestToServer - receiveCommand - " + e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            command = null;
            System.out.println("[ERR] - RequestToServer - receiveCommand - " + e.getLocalizedMessage());
        }

        System.out.println("[INF] - RequestToServer - receiveCommand - have command : " + command.getType().name());
        return command;
    }


    //------------------------------------------------------------------------
    //  receiving command from server to gate

    @Override
    public void run() {

        while (!getCommander().isExit()) {

            // create connecting with server
            open();
            if (!isConnected()) return;

            // if connected - send server gate ID
            System.out.println("[INF] - RequestToServer - run - authorization (send User ID to server)");
            if (!sendCommand(new HandshakeCommand(getGateId()))) continue;

            // working with commands from server
            Command command = null;
            do {
                // get new command
                command = receiveCommand();
                if (command == null) continue;
                // execute received command
                getCommander().execute(command);
                // get next command
            } while (!getCommander().isExit() && command != null);
        }

        // закрываем существующее соединение
        close();

    }
}
