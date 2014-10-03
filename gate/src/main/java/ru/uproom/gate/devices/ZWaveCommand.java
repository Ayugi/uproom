package ru.uproom.gate.devices;

/**
 * Реализация класса,содержащего команду управления узлом сети Z-Wave
 * <p/>
 * Created by osipenko on 05.08.14.
 */
public class ZWaveCommand {


    //##############################################################################################################
    //######    параметры класса


    private long homeId = 0;
    private String command = "";
    private short nodeId = 0;
    private int valueIndex = 0;
    private short groupId = 0;
    private short targetId = 0;
    private String valueNew = "0";
    private short level = 0;


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


    //------------------------------------------------------------------------
    //  идентификатор группы узлов

    public short getGroupId() {
        return groupId;
    }

    public void setGroupId(short groupId) {
        this.groupId = groupId;
    }


    //------------------------------------------------------------------------
    //  идентификатор целевого узла

    public short getTargetId() {
        return targetId;
    }

    public void setTargetId(short targetId) {
        this.targetId = targetId;
    }


    //------------------------------------------------------------------------
    //  новое значение для параметра

    public String getValueNew() {
        return valueNew;
    }

    public void setValueNew(String valueNew) {
        this.valueNew = valueNew;
    }


    //------------------------------------------------------------------------
    //  новое значение для уровня

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
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

                    if (parts[0].equalsIgnoreCase("NodeID")) setNodeId(Short.parseShort(parts[1]));
                    else if (parts[0].equalsIgnoreCase("ValueID")) setValueIndex(Integer.parseInt(parts[1]));
                    else if (parts[0].equalsIgnoreCase("GroupID")) setGroupId(Short.parseShort(parts[1]));
                    else if (parts[0].equalsIgnoreCase("TargetID")) setTargetId(Short.parseShort(parts[1]));
                    else if (parts[0].equalsIgnoreCase("Value")) setValueNew(parts[1]);
                    else if (parts[0].equalsIgnoreCase("Level")) setLevel(Short.parseShort(parts[1]));

                }

            }
        }

    }

}
