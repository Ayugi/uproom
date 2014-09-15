package ru.uproom.gate.handlers;

import ru.uproom.gate.transport.command.CommandType;

/**
 * Created by osipenko on 14.09.14.
 */
public @interface CommandAnnotation {
    CommandType value();
}
