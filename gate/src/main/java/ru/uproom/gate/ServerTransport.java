package ru.uproom.gate;

import ru.uproom.gate.transport.Command;

/**
 * Marker interface for sending messages objects
 * <p/>
 * Created by osipenko on 09.09.14.
 */
public interface ServerTransport {
    public boolean sendCommand(Command command);
}
