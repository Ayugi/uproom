package ru.uproom.gate.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.ControllerCallback;
import org.zwave4j.ControllerError;
import org.zwave4j.ControllerState;
import org.zwave4j.Manager;
import ru.uproom.gate.notifications.GateNotificationType;
import ru.uproom.gate.notifications.GateWatcher;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * inner class which contain list of Z-Wave devices
 * <p/>
 * Created by osipenko on 10.08.14.
 */
public class ZWaveHome implements ControllerCallback {


    //##############################################################################################################
    //######    fields

    private static final Logger LOG = LoggerFactory.getLogger(ZWaveHome.class);
    private final Map<Short, ZWaveNode> nodes = new TreeMap<Short, ZWaveNode>();
    private GateWatcher watcher;
    private long homeId;
    private boolean ready;
    private boolean failed;

    private DeviceStateEnum requestState;


    //##############################################################################################################
    //######    constructors


    public ZWaveHome() {
        this(null);
    }

    public ZWaveHome(GateWatcher watcher) {
        this.watcher = watcher;
    }


    //##############################################################################################################
    //######    getters and setters

    public void setWatcher(GateWatcher watcher) {
        this.watcher = watcher;
    }

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
        watcher.onGateEvent(GateNotificationType.SendDeviceList, null);
    }

    public boolean isFailed() {
        boolean temp = failed;
        failed = false;
        return temp;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }


    //------------------------------------------------------------------------
    // node list

    public Map<Short, ZWaveNode> getNodes() {
        return nodes;
    }


    //------------------------------------------------------------------------
    // state of Z-Wave Network Controller

    public DeviceStateEnum getControllerState() {
        DeviceStateEnum state = DeviceStateEnum.Down;
        if (homeId <= 0) return state;
        short index = Manager.get().getControllerNodeId(homeId);
        ZWaveNode node = nodes.get(index);
        if (node != null) {
            Object o = node.getParams().get(DeviceParametersNames.State);
            if (o != null && o instanceof DeviceStateEnum) state = (DeviceStateEnum) o;
        }
        return state;
    }

    public void setControllerState(DeviceStateEnum state) {
        if (homeId <= 0) return;
        short index = Manager.get().getControllerNodeId(homeId);
        ZWaveNode node = nodes.get(index);
        if (node != null)
            node.getParams().put(DeviceParametersNames.State, state);
    }


    //------------------------------------------------------------------------
    // request to state of Z-Wave Network Controller from server

    public void setRequestState(DeviceStateEnum requestState) {
        this.requestState = requestState;
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


    //------------------------------------------------------------------------
    //  callback for Z-Wave controller commands

    @Override
    public void onCallback(ControllerState controllerState, ControllerError controllerError, Object o) {

        // cancel current mode
        //if (o != null && o instanceof DeviceStateEnum && (DeviceStateEnum)o == DeviceStateEnum.Cancel) {
        if (controllerState == ControllerState.CANCEL) {
            setControllerState(DeviceStateEnum.Work);
            watcher.onGateEvent(GateNotificationType.Cancel, null);
            requestState = DeviceStateEnum.Unknown;
            return;
        }

        // set requested mode
        if (controllerState == ControllerState.WAITING && controllerError == ControllerError.NONE) {
            switch (requestState) {
                case Add:
                    setControllerState(requestState);
                    watcher.onGateEvent(GateNotificationType.AddModeOn, null);
                    break;
                case Remove:
                    setControllerState(requestState);
                    watcher.onGateEvent(GateNotificationType.RemoveModeOn, null);
                    break;
                default:
            }
            requestState = DeviceStateEnum.Unknown;
        }

        LOG.debug("Z-Wave controller : state={}, error={}", new Object[]{
                controllerState,
                controllerError
        });
    }
}
