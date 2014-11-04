package ru.uproom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.domain.Device;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.HandshakeCommand;
import ru.uproom.gate.transport.command.PingCommand;
import ru.uproom.gate.transport.command.SendDeviceListCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hedin on 30.08.2014.
 */
public class GateSocketHandler implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(GateSocketHandler.class);
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private int userId;
    private boolean stopped;
    private DeviceStorageService deviceStorage;
    private GateTransport gateTransport;

    public long getLastPingInterval() {
        return lastPingInterval;
    }

    public long getLastPingIssued() {
        return lastPingIssued;
    }

    public int getUserId() {
        return userId;
    }

    private long lastPingInterval = -1;
    private long lastPingIssued = -1;
    private ConnectionChecker checker = new ConnectionChecker();

    public GateSocketHandler(Socket socket, DeviceStorageService deviceStorage, GateTransport gateTransport) {
        this.socket = socket;
        this.deviceStorage = deviceStorage;
        this.gateTransport = gateTransport;
        prepareReaderStream();
        prepareWriterStream();
    }

    private void prepareReaderStream() {
        try {
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareWriterStream() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int handshake() {
        LOG.info("handshake");
        try {
            Object handshakeObj = input.readObject();
            if (!(handshakeObj instanceof HandshakeCommand)) {
                LOG.debug("Invalid handshake received {}", handshakeObj);
                return -1;
            }
            HandshakeCommand handshake = (HandshakeCommand) handshakeObj;
            userId = handshake.getGateId();
            deviceStorage.onNewUser(userId);
            LOG.info("handshake successful userId " + userId);
            new Thread(checker).start();
            return userId;
        } catch (IOException e) {
            throw new RuntimeException("Failed to receive handshake ", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to receive handshake ", e);
        }
    }

    public void sendCommand(Command command) {
        LOG.info("sendCommand command " + command.getType());
        try {
            output.writeObject(command);
        } catch (IOException e) {
            LOG.warn("network failure ", e);
            checker.stop();
            gateTransport.onConnectionFailure(this);
            stop();
        }
    }

    @Override
    public void run() {
        LOG.info("listen ");
        while (!stopped) {
            try {
                Command command = (Command) input.readObject();
                LOG.info("listen command " + command.getType());
                // TODO handles map
                if (command instanceof SendDeviceListCommand)
                    deviceStorage.addDevices(userId,
                            transformDtosToDevices((SendDeviceListCommand) command));
                if (command instanceof PingCommand){
                    PingCommand ping = (PingCommand) command;
                    lastPingInterval = System.currentTimeMillis()-ping.getIssued();
                    lastPingIssued = -1;
                    LOG.info("ping back " + lastPingInterval);
                }

            } catch (IOException e) {
                e.printStackTrace();
                stopped = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Device> transformDtosToDevices(SendDeviceListCommand listCommand) {
        List<Device> devices = new ArrayList<>();
        for (DeviceDTO dto : listCommand.getDevices()) {
            devices.add(new Device(dto));
        }
        return devices;
    }

    public void stop() {
        stopped = true;
    }

    private class ConnectionChecker implements Runnable{

        private boolean stopped;

        @Override
        public void run() {
            while (! stopped){
                synchronized (this) {
                    try {
                        wait(5000);
                    } catch (InterruptedException e) {
                        LOG.error("unexpected interruption", e);
                    }
                }
                sendCommand(new PingCommand());
                lastPingIssued = System.currentTimeMillis();
                LOG.info("ping issued");
            }
        }

        public void stop(){
            stopped = true;
        }
    }
}