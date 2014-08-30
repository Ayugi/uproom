package ru.uproom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.uproom.gate.transport.Command;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by HEDIN on 28.08.2014.
 */
@Service
public class GateServiceImpl implements GateTransport {

    private static final Logger LOG = LoggerFactory.getLogger(GateServiceImpl.class);

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
                    System.out.print("new connection");
                    // TODO handshake, store as user socket
                    ObjectInputStream stream = new ObjectInputStream(accept.getInputStream());
                    try {
                        while (true) {
                            Object o = stream.readObject();
                            System.out.print("handshake so");
                            LOG.info("handshake");
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

