package ru.uproom.service;

import ru.uproom.gate.transport.Command;

/**
 * Created by HEDIN on 28.08.2014.
 */
public interface GateTransport {
    void sendCommand(Command command, String userId);
}
