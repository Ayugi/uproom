package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Manager;
import ru.uproom.gate.notifications.GateWatcher;
import ru.uproom.gate.transport.command.CancelCommand;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;

/**
 * Handler for command which cancel current mode of Z-Wave controller
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.Cancel)
public class CancelCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CancelCommandHandler.class);

    @Override
    public boolean execute(Command command, final GateWatcher watcher) {

        if (command == null || !(command instanceof CancelCommand)) return false;

        LOG.debug("Receive command Cancel");

        return Manager.get().cancelControllerCommand(watcher.getHome().getHomeId());
    }

}
