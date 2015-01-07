package ru.uproom.gate.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.uproom.gate.commands.GateCommander;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.PingCommand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * class with functionality of changing data with server
 * <p/>
 * Created by osipenko on 08.08.14.
 */
@Service
public class ServerTransportImpl implements ServerTransport {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(ServerTransportImpl.class);

    @Value("${cloud_host}")
    private String host;
    @Value("${cloud_port}")
    private int port;
    @Value("${local_port}")
    private int localPort;
    @Value("${connection_attempts}")
    private int times = 0;
    @Value("${period_between_attempts}")
    private long periodBetweenAttempts = 0;
    @Value("${gateId}")
    private int gateId;

    @Value("${period_wait_ping}")
    private int periodWaitPing;

    private ConcurrentMap<String, ServerTransportUnit> transportUnits = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Thread> threadTransportUnits = new ConcurrentHashMap<>();

    private boolean running = false;
    private boolean failed = false;

    @Autowired
    private GateCommander commander;


    //##############################################################################################################
    //######    constructors


    public ServerTransportImpl() {
    }


    //------------------------------------------------------------------------
    //  initialization / reinitialization

    @PostConstruct
    public void init() {
        init(host, port);
        //init(2);
    }

    public void init(String host, int port) {

        ServerTransportUnit unit = new ServerTransportUnit(host, port, gateId, this, periodWaitPing);
        transportUnits.put(host, unit);
        Thread thread = new Thread(unit);
        threadTransportUnits.put(host, thread);
        thread.start();

    }


    //##############################################################################################################
    //######    getters & setters


    //------------------------------------------------------------------------
    //  get commander class for some business

    @Override
    public GateCommander getCommander() {
        return commander;
    }


    //------------------------------------------------------------------------
    //  get period for wait ping command

    @Override
    public long getPingPeriod() {
        return periodWaitPing;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  close connection

    @PreDestroy
    public void close() {
        for (Map.Entry<String, ServerTransportUnit> entry : transportUnits.entrySet()) {
            entry.getValue().setWork(false);
        }
        for (Map.Entry<String, Thread> entry : threadTransportUnits.entrySet()) {
            entry.getValue().interrupt();
        }
    }


    //------------------------------------------------------------------------
    //  send command to all interesting servers

    @Override
    public void sendCommand(Command command) {

        // if command has a link ID
        if (command instanceof PingCommand) {
            ServerTransportUnit unit = transportUnits.get(((PingCommand) command).getLinkId());
            if (unit != null)
                unit.sendCommand(command);

            // another commands
        } else {
            for (Map.Entry<String, ServerTransportUnit> entry : transportUnits.entrySet()) {
                entry.getValue().sendCommand(command);
            }
        }

    }


    //------------------------------------------------------------------------
    //  restart connection from outside

    @Override
    public void restartLink(String host, boolean restart) {

        ServerTransportUnit unit = transportUnits.get(host);
//        if (unit != null) unit.setWork(false);
//        transportUnits.remove(linkId, unit);
//
//        Thread thread = threadTransportUnits.get(linkId);
//        if (thread != null) thread.interrupt();
//        threadTransportUnits.remove(linkId, thread);
//
//        DelayTimer.sleep(periodBetweenAttempts);
//        LOG.debug("link id : {} - END", new Object[]{
//                linkId
//        });
//        init(linkId);
//        LOG.debug("link id : {} - POST INIT", new Object[]{
//                linkId
//        });

    }

}
