package ru.uproom.service;

import ru.uproom.gate.transport.command.Command;

/**
 * Created by HEDIN on 28.08.2014.
 */
public interface GateTransport {
    void sendCommand(Command command, int userId);
    void onConnectionFailure(GateSocketHandler handler);
}
