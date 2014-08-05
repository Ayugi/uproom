package ru.uproom.gate;

import org.zwave4j.ValueId;

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
    private ValueId valueId = null;


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


    public ValueId getValueId() {
        return valueId;
    }

    public void setValueId(ValueId valueId) {
        this.valueId = valueId;
    }
}
