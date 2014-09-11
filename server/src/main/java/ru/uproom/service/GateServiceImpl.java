package ru.uproom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.uproom.gate.transport.command.Command;

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

    private Map<String, GateSocketHandler> activeSockets = new HashMap<>();

    @Override
    public void sendCommand(Command command, String userId) {

    }

    @PostConstruct
    public void init() {
        LOG.info("INIT");
        try {
            ServerSocket serverSocket = new ServerSocket(8282);
            Thread listener = new Thread(new SocketListener(serverSocket));
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GateSocketHandler getHandler(String id) {
        return activeSockets.get(id);
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
            GateSocketHandler handler = new GateSocketHandler(accept);
            String userId = handler.handshake();
            activeSockets.put(userId, handler);
            handler.startListener();
        }
    }
}

