package ru.uproom.gate.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.HandshakeCommand;
import ru.uproom.gate.transport.command.PingCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by osipenko on 21.12.14.
 */
public class ServerTransportUnit implements Runnable {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(ServerTransportImpl.class);

    private boolean work = true;

    private ServerTransport transport;

    private String host;
    private int port;
    private int gateId;
    private int linkId;

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;


    //##############################################################################################################
    //######    constructors


    ServerTransportUnit(String host, int port, int gateId, ServerTransport transport, int linkId) {
        this.host = host;
        this.port = port;
        this.gateId = gateId;
        this.transport = transport;
        this.linkId = linkId;
    }


    //##############################################################################################################
    //######    getters & setters


    public void setWork(boolean work) {
        if (this.work && !work) stop();
        this.work = work;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  open connections

    public boolean open() {
        try {
            socket = new Socket(this.host, this.port);
            output = new ObjectOutputStream(socket.getOutputStream());
            return true;
        } catch (UnknownHostException e) {
            LOG.error("[UnknownHostException] - link id : {} - {}", new Object[]{
                    linkId,
                    e.getMessage()
            });
        } catch (IOException e) {
            LOG.error("[IOException] - link id : {} - {}", new Object[]{
                    linkId,
                    e.getMessage()
            });
        }
        return false;
    }


    //------------------------------------------------------------------------
    //  close connection

    private void stop() {

        // cloud server
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            LOG.error("[IOException] - link id : {} - {}", new Object[]{
                    linkId,
                    e.getMessage()
            });
        }
        socket = null;
        input = null;
        output = null;

    }


    //------------------------------------------------------------------------
    //  send command to server

    public void sendCommand(Command command) {
        try {
            if (output != null) output.writeObject(command);
            if (!(command instanceof PingCommand)) {
                if (command instanceof HandshakeCommand) {
                    LOG.debug("link id : {} - Done handshake with server ( Gate ID = {} )", new Object[]{
                            linkId,
                            ((HandshakeCommand) command).getGateId()
                    });
                } else
                    LOG.debug("link id : {} - Send command to server : {}", new Object[]{
                            linkId,
                            command.getType().name()
                    });
            }
        } catch (IOException e) {
            LOG.error("[IOException] - link id : {} - {}", new Object[]{
                    linkId,
                    e.getMessage()
            });
        }
    }


    //------------------------------------------------------------------------
    //  create input stream

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
    //  reader thread

    @Override
    public void run() {

        // set connection
        work = open();
        if (work) {
            sendCommand(new HandshakeCommand(gateId));
            work = getInputStream();
        }

        // read commands
        Command command = null;
        while (work) {

            // get next command
            try {
                if (input != null)
                    command = (Command) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                work = false;
                LOG.error("link id : {} - {}", new Object[]{
                        linkId,
                        e.getMessage()
                });
            }

            if (command != null) {
                if (command.getType() != CommandType.Ping)
                    LOG.debug("link id : {} - receive command : {}", new Object[]{
                            linkId,
                            command.getType().name()
                    });
                else
                    ((PingCommand) command).setLinkId(linkId);
                if (transport.getCommander() != null) transport.getCommander().execute(command);
            } else work = false;
        }

        // restart connection
        transport.restartLink(linkId);
    }

}
