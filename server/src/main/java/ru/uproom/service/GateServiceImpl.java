package ru.uproom.service;

import org.springframework.stereotype.Service;
import ru.uproom.gate.transport.Command;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by HEDIN on 28.08.2014.
 */
@Service
public class GateServiceImpl implements GateTransport{

    @Override
    public void sendCommand(Command command, String userId) {

    }

    @PostConstruct
    public void init(){
        try {
            ServerSocket serverSocket = new ServerSocket(8282);
            Thread listener = new Thread(new SocketListener(serverSocket));
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SocketListener implements Runnable{

        private ServerSocket serverSocket;
        private boolean running = true;

        private SocketListener(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        public void stop(){
            running = false;
        }


        @Override
        public void run() {
            try {
                while (running) {
                    Socket accept = serverSocket.accept();
                    System.out.print("new connection");
                    // TODO handshake, store as user socket
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
