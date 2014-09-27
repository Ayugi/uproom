package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.ControllerCommand;
import org.zwave4j.Manager;
import ru.uproom.gate.notifications.GateWatcher;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.RemoveModeOnCommand;

/**
 * Handler for command which set controller in remove device mode
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.RemoveModeOn)
public class RemoveModeOnCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RemoveModeOnCommandHandler.class);

    @Override
    public boolean execute(Command command, final GateWatcher watcher) {

        if (command == null || !(command instanceof RemoveModeOnCommand)) return false;

        LOG.debug("Receive command Remove Mode On");

        return Manager.get().beginControllerCommand(
                watcher.getHome().getHomeId(),
                ControllerCommand.REMOVE_DEVICE,
                watcher.getHome()
        );
    }

}
