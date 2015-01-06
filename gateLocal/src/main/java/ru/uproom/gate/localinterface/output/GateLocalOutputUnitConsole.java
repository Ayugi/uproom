package ru.uproom.gate.localinterface.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.transport.command.CommandType;
import ru.uproom.gate.transport.command.ExitCommand;
import ru.uproom.gate.transport.command.GetDeviceListCommand;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.DeviceType;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Main object for handling server commands
 * </p>
 * Created by osipenko on 05.08.14.
 */
public class GateLocalOutputUnitConsole implements GateLocalOutputUnit {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(GateLocalOutputUnitConsole.class);

    private ConsoleInput input;
    private Thread threadInput;
    private PrintStream output = System.out;

    private GateLocalOutput parent;


    //##############################################################################################################
    //######    constructors


    public GateLocalOutputUnitConsole(GateLocalOutput parent) {
        this.parent = parent;
        input = new ConsoleInput(this);
        threadInput = new Thread(input);
        threadInput.start();
    }


    //##############################################################################################################
    //######    getters / setters


    //##############################################################################################################
    //######    inner classes

    @Override
    public void setListDTO(List<DeviceDTO> devices) {
        output.println("\n >>>>> device list >>>>>");
        for (DeviceDTO dto : devices) {
            output.println("\tdevice id = " + dto.getZId() +
                    "; type = " + dto.getType().name() + "; parameters : ");
            for (Map.Entry<DeviceParametersNames, Object> entry : dto.getParameters().entrySet()) {
                output.println("\t\t" + entry.getKey().name() + " = " + entry.getValue() + " ;");
            }
        }
        output.println("<<<<< device list <<<<<\n");
    }


    //##############################################################################################################
    //######    methods-


    //------------------------------------------------------------------------
    //  executioner of commands from server

    public void viewHelp() {
        output.println("\n>>>>> command list (help) >>>>> ");
        output.println("\tHelp (this command)");
        output.println("\tExit (exit from program)");
        output.println("\tGetDeviceList (get device list from gate)");
        output.println("\tSetDeviceParameter, node=x, parameter=y " +
                "(set device with id 'x' parameter with name 'parameter' to value 'y')");
        output.println("<<<<< command list (help) <<<<< \n");
    }


    //------------------------------------------------------------------------
    //  view help

    @Override
    public void stop() {
        if (input != null) {
            input.stop();
            input = null;
        }
        if (output != null) {
            output.close();
            output = null;
        }
    }


    //------------------------------------------------------------------------
    //  stop program

    public void setDeviceParameter(String[] command) {

        if (command.length < 3) return;
        String[] nodeArray = command[1].split("=");
        if (nodeArray.length < 2) return;
        if (!nodeArray[0].equalsIgnoreCase("node")) return;
        int zId = Integer.parseInt(nodeArray[1]);
        String[] paramArray = command[2].split("=");
        if (paramArray.length < 2) return;
        Map<DeviceParametersNames, Object> parameters = new HashMap<>();
        parameters.put(DeviceParametersNames.valueOf(paramArray[0]), paramArray[1]);
        DeviceDTO device = new DeviceDTO(0, zId, DeviceType.None, parameters);
        parent.setCommandFromUnit(new SetDeviceParameterCommand(device));

    }


    //------------------------------------------------------------------------
    //  stop program

    public void commandFromConsole(String command) {
        if (command.isEmpty()) return;
        String[] cmdArray = command.split(", ");
        if (cmdArray.length < 1) return;

        CommandType type = CommandType.None;
        try {
            type = CommandType.valueOf(cmdArray[0]);
        } catch (NullPointerException | IllegalArgumentException e) {
            LOG.error("unknown command : {}", e.getMessage());
        }

        switch (type) {
            case GetDeviceList:
                parent.setCommandFromUnit(new GetDeviceListCommand());
                break;
            case Help:
                viewHelp();
                break;
            case Exit:
                parent.setCommandFromUnit(new ExitCommand());
                break;
            case SetDeviceParameter:
                setDeviceParameter(cmdArray);
                break;
            default:
        }
    }


    //------------------------------------------------------------------------
    //  has a data from console input

    private class ConsoleInput implements Runnable {

        private GateLocalOutputUnitConsole parent;
        private boolean stopped;

        public ConsoleInput(GateLocalOutputUnitConsole parent) {
            this.parent = parent;
        }

        public void stop() {
            stopped = true;
            Thread.currentThread().interrupt();
        }

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String command = "";
            while (!stopped) {
                try {
                    command = br.readLine();
                } catch (IOException e) {
                    LOG.error("I/O Exception \n{}", e);
                    stopped = true;
                    continue;
                }
                if (command == null) {
                    stopped = true;
                    continue;
                }
                parent.commandFromConsole(command);
            }

        }

    }

}
