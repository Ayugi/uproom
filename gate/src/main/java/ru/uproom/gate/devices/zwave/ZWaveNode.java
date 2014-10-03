package ru.uproom.gate.devices.zwave;

import org.zwave4j.Manager;
import org.zwave4j.Notification;
import ru.uproom.gate.devices.GateDevice;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.devices.ZWaveNodeCallback;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.DeviceType;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * device in Z-Wave net
 * <p/>
 * Created by osipenko on 31.07.14.
 */
public class ZWaveNode implements GateDevice {


    //=============================================================================================================
    //======    fields


    // device type
    DeviceType type;
    private GateDevicesSet home = null;
    private boolean polled = false;

    private List<Integer> groups = new ArrayList<Integer>();
    private List<ZWaveNodeCallback> events = new ArrayList<ZWaveNodeCallback>();
    // device ID in server database
    private int id;
    // device ID in Z-Wave net
    private short zId = 0;
    // device parameters
    private Map<DeviceParametersNames, Object> params =
            new EnumMap<DeviceParametersNames, Object>(DeviceParametersNames.class);


    //=============================================================================================================
    //======    constructors


    public ZWaveNode(GateDevicesSet home, Integer gateDeviceId) {

        setHome(home);
        params.put(DeviceParametersNames.GateDeviceId, gateDeviceId);
        setDeviceType(Manager.get().getNodeType(getHome().getHomeId(), zId));

    }


    //=============================================================================================================
    //======    getters and setters


    //------------------------------------------------------------------------
    //  home

    public GateDevicesSet getHome() {
        return home;
    }

    public void setHome(GateDevicesSet home) {
        this.home = home;
    }


    //------------------------------------------------------------------------
    //  polling

    public boolean isPolled() {
        return polled;
    }

    public void setPolled(boolean _polled) {
        polled = _polled;
    }


    //------------------------------------------------------------------------
    // device ID in Z-Wave net

    public short getZId() {
        return zId;
    }

    public void setZId(short zId) {
        this.zId = zId;
    }


    //------------------------------------------------------------------------
    // device ID in server database

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    //------------------------------------------------------------------------
    //  get node type in Z-Wave net

    public DeviceType getDeviceType() {
        return (DeviceType) params.get(DeviceParametersNames.ServerDeviceType);
    }

    public void setDeviceType(DeviceType type) {
        params.put(DeviceParametersNames.ServerDeviceType, type);
    }

    public void setDeviceType(String type) {
        Integer index = (Integer) params.get(DeviceParametersNames.GateDeviceId);
        if ((int) Manager.get().getControllerNodeId(home.getHomeId()) == index)
            params.put(DeviceParametersNames.ServerDeviceType, DeviceType.Controller);
        else {
            // todo : set right device type for server using
            params.put(DeviceParametersNames.ServerDeviceType, DeviceType.None);
        }
    }


    //------------------------------------------------------------------------
    //  node groups

    @Override
    public void setGroup(Integer index) {
        if (!isExistGroup(index)) groups.add(index);
    }

    @Override
    public void removeGroup(Integer index) {
        groups.remove(index);
    }

    @Override
    public boolean isExistGroup(Integer index) {
        return (groups.indexOf(index) >= 0);
    }


    //------------------------------------------------------------------------
    //  events handling

    public boolean addEvent(ZWaveNodeCallback event) {
        return events.add(event);
    }

    public boolean removeEvent(ZWaveNodeCallback event) {
        return events.remove(event);
    }

    public void callEvents(Notification notification) {
        for (ZWaveNodeCallback event : events) {
            event.onCallback(this, notification);
        }
    }


    //------------------------------------------------------------------------
    //  events handling

    @Override
    public Object getParameter(DeviceParametersNames name) {
        return params.get(name);
    }

    @Override
    public Object setParameter(DeviceParametersNames name, Object value) {
        return params.put(name, value);
    }

    public Object removeParameter(DeviceParametersNames name) {
        return params.remove(name);
    }

    public Map<DeviceParametersNames, Object> getParameters() {
        return params;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  get node values list as string

    public String getValueList() {
        String result = "[";

        boolean needComma = false;
        for (Map.Entry<DeviceParametersNames, Object> entry : params.entrySet()) {
            if (needComma) result += ",";
            else needComma = true;
            result += entry.getValue().toString();
        }
        result += "]";

        return result;
    }


    //------------------------------------------------------------------------
    //  get node information as DTO

    @Override
    public DeviceDTO getDeviceDTO() {
        DeviceDTO dto = new DeviceDTO(id, zId, type);

        Map<DeviceParametersNames, String> parameters = dto.getParameters();
        // add to map all values
        for (Map.Entry<DeviceParametersNames, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof ZWaveValue)
                parameters.put(entry.getKey(), ((ZWaveValue) entry.getValue()).getValueAsString());
            else
                parameters.put(entry.getKey(), entry.getValue().toString());
        }

        return dto;
    }

    public DeviceDTO getDeviceParameters(DeviceParametersNames[] paramNames) {
        DeviceDTO dto = new DeviceDTO(id, zId, type);

        Map<DeviceParametersNames, String> parameters = dto.getParameters();
        // add to map all values
        for (DeviceParametersNames paramName : paramNames) {
            Object param = params.get(paramName);
            if (param == null) continue;
            if (param instanceof ZWaveValue)
                parameters.put(paramName, ((ZWaveValue) param).getValueAsString());
            else
                parameters.put(paramName, param.toString());
        }

        return dto;
    }


    //------------------------------------------------------------------------
    //  получение краткой информации об узле в виде строки

    @Override
    public String toString() {
        String result = String.format("{\"id\":\"%d\",\"type\":\"%s\"}",
                params.get(DeviceParametersNames.GateDeviceId),
                params.get(DeviceParametersNames.ServerDeviceType)
        );

        return result;
    }


    //------------------------------------------------------------------------
    //  set node any values

    public boolean setParams(DeviceDTO device) {
        for (Map.Entry<DeviceParametersNames, String> entry : device.getParameters().entrySet()) {
            Object param = params.get(entry.getKey());
            if (param == null) continue;
            if (param instanceof ZWaveValue)
                ((ZWaveValue) param).setValue(entry.getValue());
            else
                params.put(entry.getKey(), entry.getValue());
        }
        return true;
    }


}
