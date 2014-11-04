package ru.uproom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.GetDeviceListCommand;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HEDIN on 28.08.2014.
 */
@Service
public class GateServiceImpl implements GateTransport {

    private static final Logger LOG = LoggerFactory.getLogger(GateServiceImpl.class);
    @Value("${port}")
    private int port;
    private Map<Integer, GateSocketHandler> activeSockets = new HashMap<>();

    @Autowired
    private DeviceStorageService deviceStorage;

    @Override
    public void sendCommand(Command command, int userId) {
        GateSocketHandler gateSocketHandler = activeSockets.get(userId);
        if (null == gateSocketHandler){
            LOG.error("gate offline for user " + userId);
            return;
        }
        gateSocketHandler.sendCommand(command);
    }

    @Override
    public void onConnectionFailure(int userId) {
        activeSockets.remove(userId);
    }

    @PostConstruct
    public void init() {
        LOG.info("INIT");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Thread listener = new Thread(new SocketListener(serverSocket, this));
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GateSocketHandler getHandler(int userId) {
        return activeSockets.get(userId);
    }

    private class SocketListener implements Runnable {

        private ServerSocket serverSocket;
        private boolean running = true;
        private GateTransport gateTransport;

        private SocketListener(ServerSocket serverSocket, GateTransport gateTransport) {
            this.serverSocket = serverSocket;
            this.gateTransport = gateTransport;
        }

        public void stop() {
            running = false;
        }


        @Override
        public void run() {
            try {
                while (running) {
                    Socket accept = serverSocket.accept();
                    handleConnection(accept);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void handleConnection(Socket accept) throws IOException {
            GateSocketHandler handler = new GateSocketHandler(accept, deviceStorage, gateTransport);
            int userId = handler.handshake();
            if (userId < 0) return;
            activeSockets.put(userId, handler);
            handler.sendCommand(new GetDeviceListCommand());
            Thread thread = new Thread(handler);
            thread.start();
        }
    }
}

