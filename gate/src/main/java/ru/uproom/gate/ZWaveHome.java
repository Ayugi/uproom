package ru.uproom.gate;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by osipenko on 10.08.14.
 */
public class ZWaveHome extends TreeMap<Short, ZWaveNode> {


    //##############################################################################################################
    //######    параметры класса


    private long homeId;


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


    //##############################################################################################################
    //######    методы класса-


    //------------------------------------------------------------------------
    //  Идентификатор совокупности помещений (дома) ассоциированного с сетью

    public String getNodeList() {
        String result = "[";

        boolean needComma = false;
        for (Map.Entry<Short, ZWaveNode> entry : this.entrySet()) {
            if (needComma) result += ",";
            else needComma = true;
            result += entry.getValue().toString();
        }
        result += "]";

        return result;
    }


    @Override
    public String toString() {
        return String.format("{\"id\":\"%d\"", homeId);
    }
}
