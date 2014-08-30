package ru.uproom.service;

import ru.uproom.gate.transport.HandshakeCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by hedin on 30.08.2014.
 */
public class GateSocketHandler {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public GateSocketHandler(Socket socket) {
        this.socket = socket;
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

    public String handshake() {
        try {
            Object handshakeObj = input.readObject();
            if (!(handshakeObj instanceof HandshakeCommand))
                throw new RuntimeException("Invalid handshake received " + handshakeObj);
            HandshakeCommand handshake = (HandshakeCommand) handshakeObj;
            return handshake.getGateId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to receive handshake ", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to receive handshake ", e);
        }
    }

    public void startListener(){

    }


}