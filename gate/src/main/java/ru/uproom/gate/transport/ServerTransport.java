package ru.uproom.gate.transport;

import ru.uproom.gate.transport.command.Command;

/**
 * Created by osipenko on 28.09.14.
 */
public interface ServerTransport {

    public void sendCommand(Command command);

    public void restartLink();

}
