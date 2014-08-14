package ru.uproom.gate;

/**
 * Реализация класса,содержащего команду управления узлом сети Z-Wave
 *
 * Created by osipenko on 05.08.14.
 */
public class ZWaveCommand {


    //##############################################################################################################
    //######    параметры класса


    private long homeId = 0;
    private String command = "";
    private short nodeId = 0;
    private int valueIndex = 0;


    //##############################################################################################################
    //######    обработка параметров класса


    //------------------------------------------------------------------------
    //  Идентификатор совокупности помещений (дома) ассоциированного с сетью

    public long getHomeId() {
        return homeId;
    }

    public void setHomeId(long homeId) {
        this.homeId = homeId;
    }


    //------------------------------------------------------------------------
    //  текст команды

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }


    //------------------------------------------------------------------------
    //  идентификатор узла


    public short getNodeId() {
        return nodeId;
    }

    public void setNodeId(short nodeId) {
        this.nodeId = nodeId;
    }


    //------------------------------------------------------------------------
    //  идентификатор параметра


    public int getValueIndex() {
        return valueIndex;
    }

    public void setValueIndex(int valueIndex) {
        this.valueIndex = valueIndex;
    }


    //##############################################################################################################
    //######    методы класса


    //------------------------------------------------------------------------
    //  получение команды из строки

    public void setCommandFromString(String rawCommand) {
        command = "";
        String[] params = rawCommand.split(", ");

        for (String param : params) {
            if (command.isEmpty()) command = param;
            else {

                String[] parts = param.split("=");
                if (parts.length > 1) {

                    if (parts[0].equalsIgnoreCase("NodeID")) nodeId = Short.parseShort(parts[1]);
                    else if (parts[0].equalsIgnoreCase("ValueID")) valueIndex = Integer.parseInt(parts[1]);

                }

            }
        }

    }


}
