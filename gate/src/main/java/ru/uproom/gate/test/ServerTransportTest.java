package ru.uproom.gate.test;

import ru.uproom.gate.domain.DelayTimer;
import ru.uproom.gate.transport.command.Command;
import ru.uproom.gate.transport.command.HandshakeCommand;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.command.ShutdownCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.DeviceType;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Class for testing link with server
 * <p/>
 * Created by osipenko on 02.09.14.
 */
public class ServerTransportTest implements AutoCloseable, Runnable {


    //##############################################################################################################
    //######    fields


    private ServerSocket server = null;
    private boolean running = false;

    private ServerTransportIn input = null;
    private ServerTransportOut output = null;


    //##############################################################################################################
    //######    inner classes


    //------------------------------------------------------------------------
    //  receive messages from gate

    public ServerTransportTest(int port) {
        try {
            server = new ServerSocket(port);
            running = true;
        } catch (IOException e) {
            System.out.println("TEST >>>> ERROR >>>> " + e.getMessage());
        }
    }


    //------------------------------------------------------------------------
    //  send commands to gate

    private void stop() {
        if (input != null) input.close();
        if (output != null) output.close();
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server = null;
        }
        input = null;
        output = null;
    }


    //##############################################################################################################
    //######    constructors

    @Override
    public void close() {
        running = false;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  stop communications

    @Override
    public void run() {

        // wait for connection
        System.out.println("TEST >>>> Waiting for gate connection ...");
        Socket socket = null;
        Thread threadOut = null;
        Thread threadIn = null;
        try {
            socket = server.accept();
            // output connection
            output = new ServerTransportOut(socket);
            // input connection
            input = new ServerTransportIn(socket);
        } catch (IOException e) {
            System.out.println("TEST >>>> ERROR >>>> " + e.getMessage());
        }
        System.out.println("TEST >>>> Gate commands connection accepted!");

        // wait for close
        while (running) {
            DelayTimer.sleep(100);
        }

        stop();
    }


    //------------------------------------------------------------------------
    //  close communications

    private class ServerTransportIn implements Runnable, AutoCloseable {

        private Socket socket = null;
        private ObjectInputStream input = null;
        private boolean running = false;
        private Thread thread = null;

        public ServerTransportIn(Socket socket) {
            this.socket = socket;
            try {
                input = new ObjectInputStream(socket.getInputStream());
                thread = new Thread(this);
                thread.start();
                running = true;
            } catch (IOException e) {
                System.out.println("TEST (ServerTransportIn) >>>> ERROR >>>> " + e.getMessage());
            }
        }

        @Override
        public void run() {
            while (running) {
                // todo : read objects from client
                Command command = null;
                try {
                    command = (Command) input.readObject();
                } catch (IOException e) {
                    System.out.println("TEST (ServerTransportIn) >>>> ERROR >>>> " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    System.out.println("TEST (ServerTransportIn) >>>> ERROR >>>> " + e.getMessage());
                    command = null;
                }
                // print info
                if (command != null)
                    System.out.println("TEST (ServerTransportIn) >>>> command type = " + command.getType().name());
                if (command != null && command instanceof HandshakeCommand)
                    System.out.println("TEST (ServerTransportIn) >>>> handshake gateId = " + ((HandshakeCommand) command).getGateId());
                if (command != null && command instanceof SetDeviceParameterCommand) {
                    SetDeviceParameterCommand deviceParameterCommand = (SetDeviceParameterCommand) command;
                    DeviceDTO device = deviceParameterCommand.getDevice();
                    System.out.println(String.format("TEST\tdevice = %d", device.getZId()));
                    for (Map.Entry<DeviceParametersNames, String> entry : device.getParameters().entrySet()) {
                        System.out.println("TEST\t\tset parameter = " + entry.getKey() + " value = " + entry.getValue());
                    }
                }
            }
            stop();
        }

        @Override
        public void close() {
            running = false;
        }

        private void stop() {
            if (input == null) return;
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            input = null;
        }

    }


    //------------------------------------------------------------------------
    //  run communications

    private class ServerTransportOut implements Runnable, AutoCloseable {

        private Socket socket = null;
        private ObjectOutputStream output = null;
        private boolean running = false;
        private Thread thread = null;

        public ServerTransportOut(Socket socket) {
            this.socket = socket;
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                thread = new Thread(this);
                thread.start();
                running = true;
            } catch (IOException e) {
                System.out.println("TEST (ServerTransportOut) >>>> ERROR >>>> " + e.getMessage());
            }
        }

        @Override
        public void run() {
            int gateId = 0;
            Boolean switchOn = false;
            while (running) {
                // write objects to client
                try {
                    // number of requests limited (~ 2 min)
                    if (gateId <= 60) {
                        //System.out.println("TEST (ServerTransportOut) >>>> send Handshake command");
                        //output.writeObject(new HandshakeCommand(String.format("gate-%06d", gateId)));
                        //System.out.println("TEST (ServerTransportOut) >>>> send GetDeviceList command");
                        //output.writeObject(new GetDeviceListCommand(String.format("gate-%06d", gateId)));
                        System.out.println("TEST (ServerTransportOut) >>>> send SetDeviceParameters command");
                        DeviceDTO device = new DeviceDTO(0, 0, (short) 2, DeviceType.BinaryPowerSwitch);
                        device.getParameters().put(DeviceParametersNames.Switch, switchOn.toString());
                        output.writeObject(new SetDeviceParameterCommand(device));
                    }
                    // request to shutdown
                    else {
                        System.out.println("TEST (ServerTransportOut) >>>> send Shutdown command");
                        output.writeObject(new ShutdownCommand(gateId));
                    }
                } catch (IOException e) {
                    System.out.println("TEST (ServerTransportOut) >>>> ERROR >>>> " + e.getMessage());
                }
                // slowdown ~ 1 sec
                DelayTimer.sleep(5000);
                // next command
                gateId++;
                switchOn = !switchOn;
            }
            stop();
        }

        @Override
        public void close() {
            running = false;
        }


        private void stop() {
            if (output == null) return;
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            output = null;
        }

    }

}
