package ru.uproom.gate.transport.dto;

import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for transfer information about object between server and gate
 * <p/>
 * Created by osipenko on 09.09.14.
 */
public class DeviceDTO implements Serializable {
    private static final long serialVersionUID = 8267730517145621555L;


    //##############################################################################################################
    //######    fields

    // device ID in server database
    private int id;
    // device ID in Z-Wave home net (if =0 then device removed)
    private int zId = 0;
    // device type in server database
    private DeviceType type = DeviceType.None;
    // parameters of device
    private Map<DeviceParametersNames, Object> parameters =
            new HashMap<DeviceParametersNames, Object>();

    private DeviceState state;


    //##############################################################################################################
    //######    constructors


    public DeviceDTO(int id, int zId, DeviceType type) {
        this(id, zId, type, new HashMap<DeviceParametersNames, Object>());
        this.id = id;
        this.zId = zId;
        this.type = type;
    }

    public DeviceDTO(int id, int zId, DeviceType type, Map<DeviceParametersNames, Object> parameters) {
        this.id = id;
        this.zId = zId;
        this.type = type;
        this.parameters = parameters;
    }


    //##############################################################################################################
    //######    getters & setters


    public int getId() {
        return id;
    }

    public int getZId() {
        return zId;
    }

    public DeviceType getType() {
        return type;
    }

    public Map<DeviceParametersNames, Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "DeviceDTO{" +
                "id=" + id +
                ", zId=" + zId +
                ", type=" + type +
                ", parameters=" + parameters +
                '}';
    }
}


