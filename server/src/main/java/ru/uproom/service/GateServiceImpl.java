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

    @Value("${port}")
    private int port = 8282;
    private static final Logger LOG = LoggerFactory.getLogger(GateServiceImpl.class);
    private Map<Integer, GateSocketHandler> activeSockets = new HashMap<>();

    @Autowired
    private DeviceStorageService deviceStorage;

    @Override
    public void sendCommand(Command command, String userId) {

    }

    @PostConstruct
    public void init() {
        LOG.info("INIT");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Thread listener = new Thread(new SocketListener(serverSocket));
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

        private SocketListener(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
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
            GateSocketHandler handler = new GateSocketHandler(accept, deviceStorage);
            int userId = handler.handshake();
            if (userId < 0) return;
            activeSockets.put(userId, handler);
            handler.sendCommand(new GetDeviceListCommand());
            Thread thread = new Thread(handler);
            thread.start();
        }
    }
}

