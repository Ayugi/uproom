package ru.uproom.gate.localinterface.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.uproom.gate.localinterface.commands.GateLocalCommander;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.GetDeviceListCommand;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by osipenko on 23.12.14.
 */

@Service
public class GateLocalTransport {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(GateLocalTransport.class);
    @Value("${local_port}")
    private int port;

    private Map<Integer, GateLocalSocketHandler> sockets = new HashMap<>();

    @Autowired
    private GateLocalCommander commander;


    //##############################################################################################################
    //######    constructors


    @PostConstruct
    public void init() {
        LOG.info("transport initialization process");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Thread listener = new Thread(new SocketListener(serverSocket, this));
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //##############################################################################################################
    //######    getters & setters


    //##############################################################################################################
    //######    inner classes

    public void onConnectionFailure(GateLocalSocketHandler handler) {
        if (sockets.get(handler.getUserId()) == handler) {
            sockets.remove(handler.getUserId());
            handler.stop();
        }
    }

    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  connection broken

    public void sendCommand(Command command) {
        if (command.getType() == CommandType.Exit) {
            for (Map.Entry<Integer, GateLocalSocketHandler> entry : sockets.entrySet()) {
                entry.getValue().stop();
            }
            commander.stop();
            System.exit(1);
        }
        for (Map.Entry<Integer, GateLocalSocketHandler> entry : sockets.entrySet()) {
            entry.getValue().sendCommand(command);
        }
    }


    //------------------------------------------------------------------------
    //  send command to gate

    private class SocketListener implements Runnable {

        private ServerSocket serverSocket;
        private boolean running = true;
        private GateLocalTransport transport;

        private SocketListener(ServerSocket serverSocket, GateLocalTransport transport) {

            this.serverSocket = serverSocket;
            this.transport = transport;
        }


        public void stop() {
            running = false;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    LOG.info("wait for connection...");
                    Socket accept = serverSocket.accept();
                    handleConnection(accept);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void handleConnection(Socket accept) throws IOException {
            GateLocalSocketHandler handler = new GateLocalSocketHandler(accept, transport, commander);
            int userId = handler.handshake();
            if (userId < 0) return;
            sockets.put(userId, handler);
            handler.sendCommand(new GetDeviceListCommand());
            Thread thread = new Thread(handler);
            thread.start();
        }
    }

}
