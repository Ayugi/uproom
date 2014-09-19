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
public class ZWaveHome {


    //##############################################################################################################
    //######    fields


    private final Map<Short, ZWaveNode> nodes = new TreeMap<Short, ZWaveNode>();
    private long homeId;
    private boolean ready;
    private boolean failed;


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

    public String getHomeIdAsString() {
        return String.format("%d", homeId);
    }


    //------------------------------------------------------------------------
    //  z-wave system ready to work

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isFailed() {
        boolean temp = failed;
        failed = false;
        return temp;
    }

    public void setFailed(boolean ready) {
        this.ready = ready;
    }


    //------------------------------------------------------------------------
    // node list

    public Map<Short, ZWaveNode> getNodes() {
        return nodes;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  create device list as string

    public String getNodeList() {
        String result = "[";

        boolean needComma = false;
        for (Map.Entry<Short, ZWaveNode> entry : nodes.entrySet()) {
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
        for (Map.Entry<Short, ZWaveNode> entry : nodes.entrySet()) {
            devices.add(entry.getValue().getDeviceInfo());
        }
        return devices;
    }


    @Override
    public String toString() {
        return String.format("{\"id\":\"%d\"", homeId);
    }


    //------------------------------------------------------------------------
    //  set any values of any node

    public boolean setValuesOfNodes(List<DeviceDTO> devices) {
        for (DeviceDTO device : devices) {
            ZWaveNode node = nodes.get(device.getZId());
            if (node == null) continue;
            node.setParams(device);
        }
        return true;
    }

}
