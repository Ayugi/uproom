package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.ControllerCommand;
import org.zwave4j.Manager;
import ru.uproom.gate.notifications.GateWatcher;
import ru.uproom.gate.transport.command.AddModeOnCommand;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;

/**
 * Handler for command which set controller in add device mode
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.AddModeOn)
public class AddModeOnCommandHandler implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AddModeOnCommandHandler.class);

    @Override
    public boolean execute(Command command, final GateWatcher watcher) {

        if (command == null || !(command instanceof AddModeOnCommand)) return false;

        LOG.debug("Receive command Add Mode On");

        return Manager.get().beginControllerCommand(
                watcher.getHome().getHomeId(),
                ControllerCommand.ADD_DEVICE,
                watcher.getHome()
        );
    }

}
