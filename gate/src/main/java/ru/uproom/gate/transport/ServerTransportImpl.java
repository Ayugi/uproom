package ru.uproom.gate.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.uproom.gate.commands.GateCommander;
import ru.uproom.gate.transport.command.Command;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

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
    @Value("${connection_attempts}")
    private int times = 0;
    @Value("${period_between_attempts}")
    private long periodBetweenAttempts = 0;
    @Value("${gateId}")
    private int gateId;

    @Value("${period_wait_ping}")
    private int periodWaitPing;

    private Map<Integer, ServerTransportUnit> transportUnits = new HashMap<>();
    private Map<Integer, Thread> threadTransportUnits = new HashMap<>();

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
        init(1);
        init(2);
    }

    public void init(int linkId) {

        switch (linkId) {
            case 1:
                transportUnits.put(linkId, new ServerTransportUnit(host, port, gateId, this, 2));
                break;
            case 2:
                transportUnits.put(linkId, new ServerTransportUnit("127.0.0.1", 8999, gateId, this, 1));
                break;
            default:
                return;
        }

        threadTransportUnits.put(linkId, new Thread(transportUnits.get(linkId)));
        threadTransportUnits.get(linkId).start();
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
        for (Map.Entry<Integer, ServerTransportUnit> entry : transportUnits.entrySet()) {
            entry.getValue().setWork(false);
        }
        for (Map.Entry<Integer, Thread> entry : threadTransportUnits.entrySet()) {
            entry.getValue().interrupt();
        }
    }


    //------------------------------------------------------------------------
    //  send command to all interesting servers

    @Override
    public void sendCommand(Command command) {
        for (Map.Entry<Integer, ServerTransportUnit> entry : transportUnits.entrySet()) {
            entry.getValue().sendCommand(command);
        }
    }


    //------------------------------------------------------------------------
    //  restart connection from outside

    @Override
    public void restartLink(int linkId) {

        ServerTransportUnit unit = transportUnits.get(linkId);
        if (unit != null) unit.setWork(false);
        init(linkId);

    }

}
