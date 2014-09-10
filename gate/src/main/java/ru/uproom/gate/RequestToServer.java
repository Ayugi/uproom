package ru.uproom.gate;

import ru.uproom.gate.transport.Command;
import ru.uproom.gate.transport.CommandType;
import ru.uproom.gate.transport.HandshakeCommand;

import java.io.IOException;

/**
 * Created by osipenko on 26.08.14.
 */
public class RequestToServer extends CommunicationWithServer {


    //------------------------------------------------------------------------
    //  send command from gate to server

    public boolean sendCommand(Command command) {

        try {
            getOutput().writeObject(command);
        } catch (IOException e) {
            System.out.println("[ERR] - RequestToServer - sendCommand - " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }


    //------------------------------------------------------------------------
    //  receive command from server to gate

    public Command receiveCommand() {

        Command command = null;
        System.out.println("[INF] - RequestToServer - receiveCommand - waiting for next command...");

        // get next command
        try {
            command = (Command) getInput().readObject();
        } catch (IOException e) {
            command = null;
            System.out.println("[ERR] - RequestToServer - receiveCommand - " + e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            command = null;
            System.out.println("[ERR] - RequestToServer - receiveCommand - " + e.getLocalizedMessage());
        }

        System.out.println("[INF] - RequestToServer - receiveCommand - have command : " + command.getType().name());
        return command;
    }


    //------------------------------------------------------------------------
    //  receiving command from server to gate

    @Override
    public void run() {

        while (!getCommander().isExit()) {

            // create connecting with server
            open();
            if (!isConnected()) return;

            // if connected - send server gate ID
            System.out.println("[INF] - RequestToServer - run - authorization (send User ID to server)");
            if (!sendCommand(new HandshakeCommand( getGateId()))) continue;

            // working with commands from server
            Command command = null;
            do {
                // get new command
                command = receiveCommand();
                if (command == null) continue;
                // execute received command
                getCommander().execute(command);
                // get next command
            } while (!getCommander().isExit() && command != null);
        }

        // закрываем существующее соединение
        close();

    }
}
