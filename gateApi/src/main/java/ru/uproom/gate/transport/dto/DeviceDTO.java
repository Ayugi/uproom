package ru.uproom.gate.transport.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for transfer information about object between server and gate
 * <p/>
 * Created by osipenko on 09.09.14.
 */
public class DeviceDTO implements Serializable {


    //##############################################################################################################
    //######    fields


    // device ID in server database
    private int id;
    // gate ID
    private long gateId;
    // device ID in Z-Wave home net (if =0 then device removed)
    private short zId = 0;
    // device type in server database
    private DeviceType type = DeviceType.None;
    // parameters of device
    private Map<String, String> parameters = new HashMap<>();


    //##############################################################################################################
    //######    constructors


    public DeviceDTO(int id, long gateId, short zId, DeviceType type) {
        this.id = id;
        this.gateId = gateId;
        this.zId = zId;
        this.type = type;
    }


    //##############################################################################################################
    //######    getters & setters


    public int getId() {
        return id;
    }

    public long getGateId() {
        return gateId;
    }

    public short getZId() {
        return zId;
    }

    public DeviceType getType() {
        return type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

}


