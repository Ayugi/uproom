package ru.uproom.gate.zwave;

import org.zwave4j.Manager;
import org.zwave4j.Notification;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.DeviceState;
import ru.uproom.gate.transport.dto.DeviceType;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * device in Z-Wave net
 * <p/>
 * Created by osipenko on 31.07.14.
 */
public class ZWaveNode extends TreeMap<Integer, ZWaveValue> {


    //=============================================================================================================
    //======    fields


    // device type
    DeviceType type;
    private ZWaveHome home = null;
    private boolean polled = false;
    private short nodeId = 0;
    private String nodeName = "";
    private String nodeLocation = "";
    private String nodeType = "";
    private String nodeProductId = "";
    private String nodeProductName = "";
    private String nodeProductType = "";
    private String nodeManufacturerId = "";
    private String nodeManufacturerName = "";
    private short nodeVersion = 0;
    private ArrayList<Short> groups = new ArrayList<Short>();
    private ArrayList<ZWaveNodeCallback> events = new ArrayList<ZWaveNodeCallback>();
    // device ID in server database
    private int id;
    // device ID in Z-Wave net
    private short zId = 0;
    // device state
    private DeviceState state;



    //=============================================================================================================
    //======    constructors


    public ZWaveNode(ZWaveHome home, short zId) {
        Manager manager = Manager.get();

        setHome(home);
        setZId(zId);
        setNodeName(manager.getNodeName(getHome().getHomeId(), zId));
        setNodeLocation(manager.getNodeLocation(getHome().getHomeId(), zId));
        // get type information
        setNodeType(manager.getNodeType(getHome().getHomeId(), zId));
        if (manager.getControllerNodeId(home.getHomeId()) == zId) this.type = DeviceType.Controller;
        // other info...
        setNodeProductId(manager.getNodeProductId(getHome().getHomeId(), zId));
        setNodeProductName(manager.getNodeProductName(getHome().getHomeId(), zId));
        setNodeProductType(manager.getNodeProductType(getHome().getHomeId(), zId));
        setNodeManufacturerId(manager.getNodeManufacturerId(getHome().getHomeId(), zId));
        setNodeManufacturerName(manager.getNodeManufacturerName(getHome().getHomeId(), zId));
        setNodeVersion(manager.getNodeVersion(getHome().getHomeId(), zId));

    }


    //=============================================================================================================
    //======    getters and setters


    //------------------------------------------------------------------------
    //  home

    public ZWaveHome getHome() {
        return home;
    }

    public void setHome(ZWaveHome home) {
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

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }


    //------------------------------------------------------------------------
    //  get node name in Z-Wave net

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }


    //------------------------------------------------------------------------
    //  get node location in Z-Wave net

    public String getNodeLocation() {
        return nodeLocation;
    }

    public void setNodeLocation(String nodeLocation) {
        this.nodeLocation = nodeLocation;
    }


    //------------------------------------------------------------------------
    //  get node product id in Z-Wave net

    public String getNodeProductId() {
        return nodeProductId;
    }

    public void setNodeProductId(String nodeProductId) {
        this.nodeProductId = nodeProductId;
    }


    //------------------------------------------------------------------------
    //  get node product name in Z-Wave net

    public String getNodeProductName() {
        return nodeProductName;
    }

    public void setNodeProductName(String nodeProductName) {
        this.nodeProductName = nodeProductName;
    }


    //------------------------------------------------------------------------
    //  get node product type in Z-Wave net

    public String getNodeProductType() {
        return nodeProductType;
    }

    public void setNodeProductType(String nodeProductType) {
        this.nodeProductType = nodeProductType;
    }


    //------------------------------------------------------------------------
    //  get node manufacturer id in Z-Wave net

    public String getNodeManufacturerId() {
        return nodeManufacturerId;
    }

    public void setNodeManufacturerId(String nodeManufacturerId) {
        this.nodeManufacturerId = nodeManufacturerId;
    }


    //------------------------------------------------------------------------
    //  get node manufacturer name in Z-Wave net

    public String getNodeManufacturerName() {
        return nodeManufacturerName;
    }

    public void setNodeManufacturerName(String nodeManufacturerName) {
        this.nodeManufacturerName = nodeManufacturerName;
    }


    //------------------------------------------------------------------------
    //  get node firmware version

    public short getNodeVersion() {
        return nodeVersion;
    }

    public void setNodeVersion(short nodeVersion) {
        this.nodeVersion = nodeVersion;
    }


    //------------------------------------------------------------------------
    //  node groups

    public boolean addGroup(Short group) {
        return groups.add(group);
    }

    public boolean removeGroup(Short group) {
        return groups.remove(group);
    }

    public boolean existGroup(Short group) {
        return (groups.indexOf(group) >= 0);
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

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState state) {
        this.state = state;
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    //  get node values list as string

    public String getValueList() {
        String result = "[";

        boolean needComma = false;
        for (Map.Entry<Integer, ZWaveValue> entry : this.entrySet()) {
            if (needComma) result += ",";
            else needComma = true;
            result += entry.getValue().toString();
        }
        result += "]";

        return result;
    }


    //------------------------------------------------------------------------
    //  get all information about node as string

    public String getNodeInfo() {
        String result = String.format("{\"id\":\"%d\"," +
                        "\"label\":\"%s\"," +
                        "\"location\":\"%s\"," +
                        "\"type\":\"%s\"," +
                        "\"productId\":\"%s\"," +
                        "\"productName\":\"%s\"," +
                        "\"productType\":\"%s\"," +
                        "\"manufacturerId\":\"%s\"," +
                        "\"manufacturerName\":\"%s\"," +
                        "}",
                getZId(),
                getNodeName(),
                getNodeLocation(),
                getNodeType(),
                getNodeProductId(),
                getNodeProductName(),
                getNodeProductType(),
                getNodeManufacturerId(),
                getNodeManufacturerName()
        );

        return result;
    }


    //------------------------------------------------------------------------
    //  get node information as DTO

    public DeviceDTO getDeviceInfo() {
        DeviceDTO dto = new DeviceDTO(id, home.getHomeId(), zId, DeviceType.None);

        Map<String, String> parameters = dto.getParameters();
        // add to map all values
        for (Map.Entry<Integer, ZWaveValue> entry : this.entrySet()) {
            parameters.put(entry.getValue().getValueLabel(), entry.getValue().getValueAsString());
        }
        // add to map needed fields
        // todo : discuss with Hedin about this conception
        parameters.put("NodeState", state.name());

        return dto;
    }


    //------------------------------------------------------------------------
    //  получение краткой информации об узле в виде строки

    @Override
    public String toString() {
        String result = String.format("{\"id\":\"%d\",\"label\":\"%s\",\"location\":\"%s\",\"type\":\"%s\"}",
                getZId(),
                getNodeName(),
                getNodeLocation(),
                getNodeType()
        );

        return result;
    }
}
