package ru.uproom.gate.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.ServerTransportWatchDog;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.PingCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for command which set controller in add device mode
 * </p>
 * Created by osipenko on 09.09.14.
 */
@CommandHandlerAnnotation(value = CommandType.Ping)
public class PingCommandHandler implements CommandHandler {


    //##############################################################################################################
    //######    fields

    private static final Logger LOG = LoggerFactory.getLogger(PingCommandHandler.class);

    private Map<Integer, ServerTransportWatchDog> watchdog = new HashMap<>();
    private Map<Integer, Thread> threadWatchdog = new HashMap<>();
    private Map<Integer, Integer> watchDogCounter = new HashMap<>();


    //##############################################################################################################
    //######    constructors


    //##############################################################################################################
    //######    getters and setters


    //##############################################################################################################
    //######    inner classes


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  listener for check data exchange

    private void startWatchDog(int watchDogId, GateDevicesSet home) {

        if (watchdog.get(watchDogId) != null) return;
        watchdog.put(watchDogId, new ServerTransportWatchDog(
                home, watchDogId, home.getTransport().getPingPeriod()));
        threadWatchdog.put(watchDogId, new Thread(watchdog.get(watchDogId)));
        threadWatchdog.get(watchDogId).start();

    }


    //------------------------------------------------------------------------
    //  handle for ping command

    @Override
    public boolean execute(Command command, GateDevicesSet home) {

        if (!(command instanceof PingCommand)) return false;

        int watchDogId = ((PingCommand) command).getLinkId();
        startWatchDog(watchDogId, home);
        ServerTransportWatchDog watchDog = watchdog.get(watchDogId);
        if (watchDog != null) {
            watchDog.setWatchDogOn(true);
            watchDog.incWatchDogCounter();
        }
        home.getTransport().sendCommand(command != null ? command : new PingCommand());

        return true;
    }


    //------------------------------------------------------------------------
    //  end of work

    @Override
    public void stop() {
        for (Map.Entry<Integer, ServerTransportWatchDog> entry : watchdog.entrySet()) {
            entry.getValue().stop();
        }
    }

}
