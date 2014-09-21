package ru.uproom.service;

import junit.framework.Assert;
import org.junit.Test;
import ru.uproom.gate.transport.command.HandshakeCommand;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by hedin on 30.08.2014.
 */
//@Ignore
public class TestSocketConnect {

    @Test
    public synchronized void testSocketHandshake() throws IOException, InterruptedException {
        GateServiceImpl service = new GateServiceImpl();
        service.init();

        Socket socket = new Socket("localhost", GateServiceImpl.PORT);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(new HandshakeCommand(1));
        wait(100);
        GateSocketHandler handler = service.getHandler(1);
        Assert.assertNotNull(handler);
    }
}
