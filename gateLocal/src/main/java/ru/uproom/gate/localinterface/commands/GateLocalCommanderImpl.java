package ru.uproom.gate.localinterface.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.uproom.gate.localinterface.output.GateLocalOutput;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.domain.ClassesSearcher;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;


/**
 * Main object for handling server commands
 * </p>
 * Created by osipenko on 05.08.14.
 */
@Service
public class GateLocalCommanderImpl implements GateLocalCommander {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(GateLocalCommanderImpl.class);

    private Map<CommandType, GateLocalCommandHandler> commandHandlers =
            new EnumMap<CommandType, GateLocalCommandHandler>(CommandType.class);

    @Autowired
    private GateLocalOutput output;


    //##############################################################################################################
    //######    constructors


    @PostConstruct
    private void prepareCommandHandlers() {

        getCommandHandlersFromPath();

    }

    private boolean getCommandHandlersFromPath() {

        for (Class<?> handler : ClassesSearcher.getAnnotatedClasses(
                GateLocalCommandHandlerAnnotation.class
        )) {
            GateLocalCommandHandlerAnnotation annotation =
                    handler.getAnnotation(GateLocalCommandHandlerAnnotation.class);
            if (annotation == null) continue;
            commandHandlers.put(
                    annotation.value(),
                    (GateLocalCommandHandler) ClassesSearcher.instantiate(handler)
            );
        }

        return commandHandlers.isEmpty();
    }


    //##############################################################################################################
    //######    getters / setters


    //##############################################################################################################
    //######    methods-


    //------------------------------------------------------------------------
    //  executioner of commands from server

    @Override
    public boolean execute(Command command) {

        GateLocalCommandHandler handler = commandHandlers.get(command.getType());
        if (handler == null) {
            LOG.error("Handler for command '{}' not found", command.getType());
            return false;
        }
        handler.execute(command, output);

        return true;
    }


    //------------------------------------------------------------------------
    //  executioner of commands from server

    @Override
    public void stop() {
        commandHandlers.clear();
    }
}
