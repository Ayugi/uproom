package ru.uproom.gate.transport;

import ru.uproom.gate.transport.command.Command;

/**
 * Marker interface for sending messages objects
 * <p/>
 * Created by osipenko on 09.09.14.
 */
public interface ServerTransportMarker {
    public boolean sendCommand(Command command);
}
