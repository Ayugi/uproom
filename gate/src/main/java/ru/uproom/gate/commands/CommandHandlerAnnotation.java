package ru.uproom.gate.commands;

import ru.uproom.gate.transport.command.CommandType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by osipenko on 14.09.14.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandlerAnnotation {
    CommandType value();
}
