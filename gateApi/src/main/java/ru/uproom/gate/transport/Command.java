package ru.uproom.gate.transport;

import java.io.Serializable;

/**
 * Created by HEDIN on 28.08.2014.
 */
public class Command implements Serializable{
    private CommandType type;

    public Command(CommandType type) {
        this.type = type;
    }

    public CommandType getType() {
        return type;
    }
}
