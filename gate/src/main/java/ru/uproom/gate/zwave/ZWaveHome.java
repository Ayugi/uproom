package ru.uproom.gate.zwave;

import ru.uproom.gate.transport.dto.DeviceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * inner class which contain list of Z-Wave devices
 * <p/>
 * Created by osipenko on 10.08.14.
 */
public class ZWaveHome extends TreeMap<Short, ZWaveNode> {


    //##############################################################################################################
    //######    fields


    private long homeId;


    //##############################################################################################################
    //######    getters and setters


    //------------------------------------------------------------------------
    //  home ID

    public long getHomeId() {
        return homeId;
    }

    public void setHomeId(long homeId) {
        this.homeId = homeId;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  create device list as string

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


    //------------------------------------------------------------------------
    //  create device list as list of serializable objects

    public List<DeviceDTO> getDeviceList() {
        List<DeviceDTO> devices = new ArrayList<DeviceDTO>();
        for (Map.Entry<Short, ZWaveNode> entry : this.entrySet()) {
            devices.add(entry.getValue().getDeviceInfo());
        }
        return devices;
    }


    @Override
    public String toString() {
        return String.format("{\"id\":\"%d\"", homeId);
    }
}
