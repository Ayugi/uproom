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
    // device state
    private DeviceStateEnum state = DeviceStateEnum.Down;
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
    //  node groups
    public void setGroup(Integer index) {
        if (!isExistGroup(index)) groups.add(index);
    }

    public boolean isExistGroup(Integer index) {
        return (groups.indexOf(index) >= 0);
    }


    //------------------------------------------------------------------------
    //  events handling

    public Object addParameter(ZWaveDeviceParametersNames name, Object value) {
        return params.put(name, value);
    }

    public Object removeParameter(ZWaveDeviceParametersNames name) {
        return params.remove(name);
    }

    public Map<ZWaveDeviceParametersNames, Object> getParameters() {
        return params;
    }


    //------------------------------------------------------------------------
    // node state

    public DeviceStateEnum getState() {
        return state;
    }

    public void setState(DeviceStateEnum state) {
        this.state = state;
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

        // todo : this code must be parted to different classes

        // switch
        Object o = params.get(ZWaveDeviceParametersNames.Switch);
        if (o != null && o instanceof ZWaveValue) {
            parameters.put(DeviceParametersNames.Switch, ((ZWaveValue) o).getValueAsBool());
        }

        // level
        o = params.get(ZWaveDeviceParametersNames.Level);
        if (o != null && o instanceof ZWaveValue) {
            parameters.put(DeviceParametersNames.Level, ((ZWaveValue) o).getValueAsInt());
        }

        // color
        o = params.get(ZWaveDeviceParametersNames.Color);
        if (o != null && o instanceof ZWaveValue) {
            parameters.put(DeviceParametersNames.Color, ((ZWaveValue) o).getValueAsInt());
        } else {
            ZWaveValue levelRed = (ZWaveValue) params.get(ZWaveDeviceParametersNames.LevelRed);
            ZWaveValue levelGreen = (ZWaveValue) params.get(ZWaveDeviceParametersNames.LevelGreen);
            ZWaveValue levelBlue = (ZWaveValue) params.get(ZWaveDeviceParametersNames.LevelBlue);
            if (levelRed != null && levelGreen != null && levelBlue != null) {
                Integer color = (256 * 256 * levelRed.getValueAsInt()) +
                        (256 * levelGreen.getValueAsInt()) + levelBlue.getValueAsInt();
                parameters.put(DeviceParametersNames.Color, color);
            }
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

        // todo : this code must be parted to different classes

        // switch
        Object o = dto.getParameters().get(DeviceParametersNames.Switch);
        if (o != null) {
            ZWaveValue param = (ZWaveValue) params.get(ZWaveDeviceParametersNames.Switch);
            if (param != null) {
                param.setValue(o.toString());
            }
        }

        // level
        o = dto.getParameters().get(DeviceParametersNames.Level);
        if (o != null) {
            ZWaveValue param = (ZWaveValue) params.get(ZWaveDeviceParametersNames.Level);
            if (param != null) {
                param.setValue(o.toString());
            }
        }

        // color
        o = dto.getParameters().get(DeviceParametersNames.Color);
        if (o != null) {
            ZWaveValue param = (ZWaveValue) params.get(ZWaveDeviceParametersNames.Color);
            if (param != null) {
                param.setValue(o.toString());
            } else {
                Integer newColor = (Integer) o;
                ZWaveValue levelRed = (ZWaveValue) params.get(ZWaveDeviceParametersNames.LevelRed);
                ZWaveValue levelGreen = (ZWaveValue) params.get(ZWaveDeviceParametersNames.LevelGreen);
                ZWaveValue levelBlue = (ZWaveValue) params.get(ZWaveDeviceParametersNames.LevelBlue);
                if (levelRed != null && levelGreen != null && levelBlue != null) {
                    levelBlue.setValue(String.valueOf(newColor % 256));
                    levelGreen.setValue(String.valueOf(newColor / 256 % 256));
                    levelRed.setValue(String.valueOf(newColor / 256 / 256 % 256));
                }
            }
        }

        // server ID
        if (dto.getId() > 0) id = dto.getId();

        return true;
    }

}
