package ru.uproom.gate.transport.command;

import ru.uproom.gate.transport.dto.DeviceDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HEDIN on 28.08.2014.
 */
public class Command implements Serializable {
    private CommandType type;
    public Command(CommandType type) {
        this.type = type;
    }

    public CommandType getType() {
        return type;
    }
}
