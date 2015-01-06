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
    // device type
    private DeviceType type = DeviceType.None;
    // server device type
    private DeviceType serverType = DeviceType.None;
    // server device type
    private int state = 0;
    // device parameters
    private Map<ZWaveDeviceParametersNames, Object> params =
            new EnumMap<>(ZWaveDeviceParametersNames.class);


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
        if ((int) Manager.get().getControllerNodeId(home.getHomeId()) == zId) {
            type = DeviceType.Controller;
            serverType = DeviceType.Controller;
        } else {
            type = DeviceType.byStringKey(Manager.get().getNodeType(home.getHomeId(), (short) zId));
            if (Manager.get().getNodeProductName(home.getHomeId(), (short) zId).contains("RGBW"))
                type = DeviceType.Rgbw;
            serverType = type;
        }
    }


    //------------------------------------------------------------------------
    //  set node manufacturer name in Z-Wave net

    public void setManufacturerName() {
        params.put(
                ZWaveDeviceParametersNames.ManufacturerName,
                Manager.get().getNodeManufacturerName(home.getHomeId(), (short) zId)
        );
        params.put(
                ZWaveDeviceParametersNames.ManufacturerId,
                Manager.get().getNodeManufacturerId(home.getHomeId(), (short) zId)
        );
    }


    //------------------------------------------------------------------------
    //  set node product name in Z-Wave net

    public void setProductName() {
        params.put(
                ZWaveDeviceParametersNames.ProductName,
                Manager.get().getNodeProductName(home.getHomeId(), (short) zId)
        );
        params.put(
                ZWaveDeviceParametersNames.ProductId,
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

    public Object setParameter(ZWaveDeviceParametersNames name, Object value) {
        return params.put(name, value);
    }

    public Object removeParameter(ZWaveDeviceParametersNames name) {
        return params.remove(name);
    }

    public Map<ZWaveDeviceParametersNames, Object> getParameters() {
        return params;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  get node information as DTO

    public DeviceDTO getDeviceDTO() {
        return getDeviceParameters(new ArrayList<>(params.keySet()).
                toArray(new ZWaveDeviceParametersNames[params.keySet().size()]));
    }

    public DeviceDTO getDeviceParameters(ZWaveDeviceParametersNames... paramNames) {

        DeviceDTO dto = new DeviceDTO(id, zId, serverType);

        Map<DeviceParametersNames, Object> parameters = dto.getParameters();
        // todo: add to map all values
        for (ZWaveDeviceParametersNames paramName : paramNames) {
            Object param = params.get(paramName);
            if (param == null) continue;
            if (param instanceof ZWaveValue)
                //parameters.put(paramName, ((ZWaveValue) param).getValueAsString());
                parameters.put(DeviceParametersNames.Unknown, ((ZWaveValue) param).getValueAsString());
            else if (param instanceof DeviceType)
                parameters.put(DeviceParametersNames.Unknown, ((DeviceType) param).name());
            else
                parameters.put(DeviceParametersNames.Unknown, param.toString());
        }

        return dto;
    }


    //------------------------------------------------------------------------
    //  получение краткой информации об узле в виде строки

    @Override
    public String toString() {
        return String.format("{\"id\":\"%d\",\"type\":\"%s\",\"serverType\":\"%s\"}",
                zId, type, serverType);
    }


    //------------------------------------------------------------------------
    //  set node any values

    public boolean setParams(DeviceDTO dto) {

        for (Map.Entry<DeviceParametersNames, Object> entry : dto.getParameters().entrySet()) {
            // todo: add to map all values
            Object param = params.get(entry.getKey());
            //if (param == null || entry.getKey().isReadOnly()) continue;
            if (param == null) continue;
            if (param instanceof ZWaveValue)
                ((ZWaveValue) param).setValue(entry.getValue().toString());
            else if (param instanceof DeviceStateEnum)
                //params.put(entry.getKey(), DeviceStateEnum.fromString(entry.getValue()));
                params.put(ZWaveDeviceParametersNames.Unknown, DeviceStateEnum.fromString(entry.getValue().toString()));
            else
                //params.put(entry.getKey(), entry.getValue());
                params.put(ZWaveDeviceParametersNames.Unknown, entry.getValue());
        }
        // some hard code
        if (dto.getId() > 0) id = dto.getId();

        return true;
    }

}
