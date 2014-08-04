package ru.uproom.gate;

import org.zwave4j.ValueId;

import java.util.ArrayList;

/**
 * Устройство в сети ZWave
 * <p/>
 * Created by osipenko on 31.07.14.
 */
public class ZwaveNode extends ArrayList<ValueId> {


    //=============================================================================================================
    //======    параметры класса

    private boolean polled = false;
    private short nodeId = 0;


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


}
