package ru.uproom.gate.localinterface.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.uproom.gate.localinterface.transport.GateLocalTransport;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.dto.DeviceDTO;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


/**
 * Main object for handling server commands
 * </p>
 * Created by osipenko on 05.08.14.
 */
@Service
public class GateLocalOutputImpl implements GateLocalOutput {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(GateLocalOutputImpl.class);

    private List<GateLocalOutputUnit> outputUnits = new ArrayList<>();

    @Autowired
    private GateLocalTransport transport;


    //##############################################################################################################
    //######    constructors


    @PostConstruct
    public void init() {
        outputUnits.add(new GateLocalOutputUnitConsole(this));
    }


    //##############################################################################################################
    //######    getters / setters


    //##############################################################################################################
    //######    methods-


    //------------------------------------------------------------------------
    //  executioner of commands from server

    @Override
    public void setListDTO(List<DeviceDTO> devices) {
        for (GateLocalOutputUnit unit : outputUnits) {
            unit.setListDTO(devices);
        }
    }

    @Override
    public void setCommandFromUnit(Command command) {
        if (command.getType() == CommandType.Exit)
            for (GateLocalOutputUnit unit : outputUnits) {
                unit.stop();
            }
        transport.sendCommand(command);
    }
}
