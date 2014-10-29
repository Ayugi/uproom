package ru.uproom.gate.devices.zwave;

import org.zwave4j.Manager;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.DeviceType;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * device in Z-Wave net
 * <p/>
 * Created by osipenko on 31.07.14.
 */
public class ZWaveNode {


    //=============================================================================================================
    //======    fields


    // device type
    private GateDevicesSet home = null;

    private List<Integer> groups = new ArrayList<>();
    // device ID in server database
    private int id;
    // device ID in Z-Wave net
    private int zId = 0;
    // device parameters
    private Map<DeviceParametersNames, Object> params =
            new EnumMap<>(DeviceParametersNames.class);


    //=============================================================================================================
    //======    constructors


    public ZWaveNode(GateDevicesSet home, Integer gateDeviceId) {

        setHome(home);
        zId = gateDeviceId;
        id = 0;
        setDeviceType();
        setManufacturerName();
        setProductName();

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
    // device ID in Z-Wave net

    public void setZId(int zId) {
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
    //  set node type in Z-Wave net

    public void setDeviceType() {
        if ((int) Manager.get().getControllerNodeId(home.getHomeId()) == zId)
            params.put(DeviceParametersNames.ServerDeviceType, DeviceType.Controller);
        else {
            params.put(
                    DeviceParametersNames.ServerDeviceType,
                    DeviceType.byStringKey(Manager.get().getNodeType(home.getHomeId(), (short) zId))
            );
        }
    }


    //------------------------------------------------------------------------
    //  set node manufacturer name in Z-Wave net

    public void setManufacturerName() {
        params.put(
                DeviceParametersNames.ManufacturerName,
                Manager.get().getNodeManufacturerName(home.getHomeId(), (short) zId)
        );
        params.put(
                DeviceParametersNames.ManufacturerId,
                Manager.get().getNodeManufacturerId(home.getHomeId(), (short) zId)
        );
    }


    //------------------------------------------------------------------------
    //  set node product name in Z-Wave net

    public void setProductName() {
        params.put(
                DeviceParametersNames.ProductName,
                Manager.get().getNodeProductName(home.getHomeId(), (short) zId)
        );
        params.put(
                DeviceParametersNames.ProductId,
                Manager.get().getNodeProductId(home.getHomeId(), (short) zId)
        );
    }


    //------------------------------------------------------------------------
    //  node groups
    public void setGroup(Integer index) {
        if (!isExistGroup(index)) groups.add(index);
    }

    public boolean isExistGroup(Integer index) {
        return (groups.indexOf(index) >= 0);
    }


    //------------------------------------------------------------------------
    //  events handling

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
    //  get node information as DTO

    public DeviceDTO getDeviceDTO() {
        return getDeviceParameters(new ArrayList<>(params.keySet()).
                toArray(new DeviceParametersNames[params.keySet().size()]));
    }

    public DeviceDTO getDeviceParameters(DeviceParametersNames... paramNames) {

        DeviceDTO dto = new DeviceDTO(id, zId, (DeviceType) params.get(DeviceParametersNames.ServerDeviceType));

        Map<DeviceParametersNames, String> parameters = dto.getParameters();
        // add to map all values
        for (DeviceParametersNames paramName : paramNames) {
            Object param = params.get(paramName);
            if (param == null) continue;
            if (param instanceof ZWaveValue)
                parameters.put(paramName, ((ZWaveValue) param).getValueAsString());
            else if (param instanceof DeviceType)
                parameters.put(paramName, ((DeviceType) param).name());
            else
                parameters.put(paramName, param.toString());
        }

        return dto;
    }


    //------------------------------------------------------------------------
    //  получение краткой информации об узле в виде строки

    @Override
    public String toString() {
        return String.format("{\"id\":\"%d\",\"type\":\"%s\"}",
                zId, params.get(DeviceParametersNames.ServerDeviceType));
    }


    //------------------------------------------------------------------------
    //  set node any values

    public boolean setParams(DeviceDTO dto) {

        for (Map.Entry<DeviceParametersNames, String> entry : dto.getParameters().entrySet()) {
            Object param = params.get(entry.getKey());
            if (param == null || entry.getKey().isReadOnly()) continue;
            if (param instanceof ZWaveValue)
                ((ZWaveValue) param).setValue(entry.getValue());
            else if (param instanceof DeviceStateEnum)
                params.put(entry.getKey(), DeviceStateEnum.fromString(entry.getValue()));
            else
                params.put(entry.getKey(), entry.getValue());
        }
        // some hard code
        id = dto.getId();

        return true;
    }

}
