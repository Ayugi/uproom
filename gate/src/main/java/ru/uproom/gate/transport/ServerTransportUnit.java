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


    private static final Logger LOG = LoggerFactory.getLogger(ServerTransportUnit.class);

    private boolean work = true;

    private ServerTransport transport;

    private String host;
    private int port;
    private int gateId;
    private long period;

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private ConnectionChecker checker;


    //##############################################################################################################
    //######    constructors


    ServerTransportUnit(String host, int port, int gateId, ServerTransport transport, long period) {
        this.host = host;
        this.port = port;
        this.gateId = gateId;
        this.transport = transport;
        this.period = period;
    }


    //##############################################################################################################
    //######    inner classes

    public void setWork(boolean work) {
        if (this.work && !work) stop(false);
        this.work = work;
    }


    //##############################################################################################################
    //######    getters & setters

    public boolean open() {
        try {
            socket = new Socket(this.host, this.port);
            output = new ObjectOutputStream(socket.getOutputStream());
            return true;
        } catch (UnknownHostException e) {
            LOG.error("[UnknownHostException] host : {} - {}", new Object[]{
                    host,
                    e.getMessage()
            });
        } catch (IOException e) {
            LOG.error("[IOException] host : {} - {}", new Object[]{
                    host,
                    e.getMessage()
            });
        }
        return false;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  open connections

    private void stop(boolean restart) {

        checker.stop();
        checker.notify();

        work = false;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            LOG.error("[IOException] - host : {} - {}", new Object[]{
                    host,
                    e.getMessage()
            });
        }
        socket = null;
        input = null;
        output = null;

        transport.restartLink(host, restart);

    }


    //------------------------------------------------------------------------
    //  close connection

    public void sendCommand(Command command) {
        try {
            if (output != null) output.writeObject(command);
            if (!(command instanceof PingCommand)) {
                if (command instanceof HandshakeCommand) {
                    LOG.debug("host : {} - Done handshake with server ( Gate ID = {} )", new Object[]{
                            host,
                            ((HandshakeCommand) command).getGateId()
                    });
                } else
                    LOG.debug("host : {} - Send command to server : {}", new Object[]{
                            host,
                            command.getType().name()
                    });
            }
        } catch (IOException e) {
            LOG.error("[IOException] - host : {} - {}", new Object[]{
                    host,
                    e.getMessage()
            });
        }
    }


    //------------------------------------------------------------------------
    //  send command to server

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

    public void backPingToServer(Command ping) {

//        if (checker == null) {
//            checker = new ConnectionChecker(this);
//            new Thread(checker).start();
//        }
        sendCommand(ping);
        LOG.debug("host : {} - send back command : {}", new Object[]{
                host,
                ping.getType().name()
        });

    }


    //------------------------------------------------------------------------
    //  create input stream

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
                LOG.error("host : {} - {}", new Object[]{
                        host,
                        e.getMessage()
                });
            }

            if (command != null) {
                if (command.getType() != CommandType.Ping) {
                    LOG.debug("host : {} - receive command : {}", new Object[]{
                            host,
                            command.getType().name()
                    });
                    if (transport.getCommander() != null) transport.getCommander().execute(command);
                } else {
                    LOG.debug("host : {} - receive command : {}", new Object[]{
                            host,
                            command.getType().name()
                    });
                    backPingToServer(command);
                }
            } else work = false;
        }

        // restart connection
        LOG.debug("host : {} - RESTART", new Object[]{
                host
        });
        stop(true);
    }


    //------------------------------------------------------------------------
    //  reader thread

    private class ConnectionChecker implements Runnable {

        private boolean stopped;
        private long currentTime;
        private ServerTransportUnit parent;

        public ConnectionChecker(ServerTransportUnit parent) {
            this.parent = parent;
        }

        public void stop() {
            stopped = true;
        }

        private void waitForNotify(long period) {
            try {
                wait(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            LOG.info("gate have a ping command from server ({}) - LINK SET ON", new Object[]{
                    host
            });

            while (!stopped) {
                synchronized (this) {
                    currentTime = System.currentTimeMillis();
                    waitForNotify(period);
                    if (stopped) continue;
                    if (System.currentTimeMillis() - currentTime > period) {
                        LOG.info("gate lost a ping command from server ({}) - LINK SET OFF", new Object[]{
                                host
                        });
                        parent.stop(false);
                    }
                }
            }

        }
    }

}
