package ru.uproom.service;

import junit.framework.Assert;
import org.junit.Test;
import ru.uproom.gate.transport.HandshakeCommand;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by hedin on 30.08.2014.
 */
public class TestSocketConnect {

    public static final String TEST_GATE_NAME = "testGate";

    @Test
    public void testSocketHandshake(){
        GateServiceImpl service = new GateServiceImpl();
        service.init();
        try {
            Socket socket = new Socket("localhost", 8282);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new HandshakeCommand(TEST_GATE_NAME));
            GateSocketHandler handler = service.getHandler(TEST_GATE_NAME);
            Assert.assertNotNull(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
