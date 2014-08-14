package ru.uproom.gate;

import java.util.Map;
import java.util.TreeMap;

/**
 * Устройство в сети ZWave
 * <p/>
 * Created by osipenko on 31.07.14.
 */
public class ZWaveNode extends TreeMap<Integer, ZWaveValue> {


    //=============================================================================================================
    //======    параметры класса

    private boolean polled = false;
    private short nodeId = 0;
    private String nodeName = "";
    private String nodeLocation = "";
    private String nodeType = "";


    //=============================================================================================================
    //======    обработка параметров класса


    //------------------------------------------------------------------------
    //  периодический опрос параметров узла

    public boolean getPolled() {
        return polled;
    }

    public void setPolled(boolean _polled) {
        polled = _polled;
    }


    //------------------------------------------------------------------------
    //  идентификатор узла

    public short getNodeId() {
        return nodeId;
    }

    public void setNodeId(short _nodeId) {
        nodeId = _nodeId;
    }


    //------------------------------------------------------------------------
    //  тип узла

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }


    //------------------------------------------------------------------------
    //  наименование узла

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }


    //------------------------------------------------------------------------
    //  местоположение узла

    public String getNodeLocation() {
        return nodeLocation;
    }

    public void setNodeLocation(String nodeLocation) {
        this.nodeLocation = nodeLocation;
    }


    //##############################################################################################################
    //######    методы класса-


    //------------------------------------------------------------------------
    //  получение значения параметра в виде строки

    public String getValueList() {
        String result = "[";

        boolean needComma = false;
        for (Map.Entry<Integer, ZWaveValue> entry : this.entrySet()) {
            if (needComma) result += ",";
            else needComma = true;
            result += entry.getValue().toString();
        }
        result += "]";

        return result;
    }


    //------------------------------------------------------------------------
    //  получение значения параметра в виде строки

    @Override
    public String toString() {
        String result = String.format("{\"id\":\"%d\",\"label\":\"%s\",\"location\":\"%s\",\"type\":\"%s\"}",
                nodeId,
                nodeName,
                nodeLocation,
                nodeType
        );

        return result;
    }

}
