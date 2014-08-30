package ru.uproom.gate;

import org.zwave4j.Manager;
import org.zwave4j.Notification;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Устройство в сети ZWave
 * <p/>
 * Created by osipenko on 31.07.14.
 */
public class ZWaveNode extends TreeMap<Integer, ZWaveValue> {


    //=============================================================================================================
    //======    параметры класса


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


    //=============================================================================================================
    //======    конструктор


    public ZWaveNode(ZWaveHome home, short nodeId) {
        Manager manager = Manager.get();

        setHome(home);
        setNodeId(nodeId);
        setNodeName(manager.getNodeName(getHome().getHomeId(), nodeId));
        setNodeLocation(Manager.get().getNodeLocation(getHome().getHomeId(), nodeId));
        setNodeType(Manager.get().getNodeType(getHome().getHomeId(), nodeId));
        setNodeProductId(Manager.get().getNodeProductId(getHome().getHomeId(), nodeId));
        setNodeProductName(Manager.get().getNodeProductName(getHome().getHomeId(), nodeId));
        setNodeProductType(Manager.get().getNodeProductType(getHome().getHomeId(), nodeId));
        setNodeManufacturerId(Manager.get().getNodeManufacturerId(getHome().getHomeId(), nodeId));
        setNodeManufacturerName(Manager.get().getNodeManufacturerName(getHome().getHomeId(), nodeId));
        setNodeVersion(Manager.get().getNodeVersion(getHome().getHomeId(), nodeId));

    }


    //=============================================================================================================
    //======    обработка параметров класса


    //------------------------------------------------------------------------
    //  идентификатор дома

    public ZWaveHome getHome() {
        return home;
    }

    public void setHome(ZWaveHome home) {
        this.home = home;
    }


    //------------------------------------------------------------------------
    //  периодический опрос параметров узла

    public boolean getPolled() {
        return polled;
    }

    public void setPolled(boolean _polled) {
        polled = _polled;
    }


    //------------------------------------------------------------------------
    //  идентификатор узла

    public short getNodeId() {
        return nodeId;
    }

    public void setNodeId(short _nodeId) {
        nodeId = _nodeId;
    }


    //------------------------------------------------------------------------
    //  тип узла

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }


    //------------------------------------------------------------------------
    //  наименование узла

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }


    //------------------------------------------------------------------------
    //  местоположение узла

    public String getNodeLocation() {
        return nodeLocation;
    }

    public void setNodeLocation(String nodeLocation) {
        this.nodeLocation = nodeLocation;
    }


    //------------------------------------------------------------------------
    //  идентификатор продукта

    public String getNodeProductId() {
        return nodeProductId;
    }

    public void setNodeProductId(String nodeProductId) {
        this.nodeProductId = nodeProductId;
    }


    //------------------------------------------------------------------------
    //  наименование продукта

    public String getNodeProductName() {
        return nodeProductName;
    }

    public void setNodeProductName(String nodeProductName) {
        this.nodeProductName = nodeProductName;
    }


    //------------------------------------------------------------------------
    //  тип продукта

    public String getNodeProductType() {
        return nodeProductType;
    }

    public void setNodeProductType(String nodeProductType) {
        this.nodeProductType = nodeProductType;
    }


    //------------------------------------------------------------------------
    //  идентификатор производителя

    public String getNodeManufacturerId() {
        return nodeManufacturerId;
    }

    public void setNodeManufacturerId(String nodeManufacturerId) {
        this.nodeManufacturerId = nodeManufacturerId;
    }


    //------------------------------------------------------------------------
    //  наименование производителя

    public String getNodeManufacturerName() {
        return nodeManufacturerName;
    }

    public void setNodeManufacturerName(String nodeManufacturerName) {
        this.nodeManufacturerName = nodeManufacturerName;
    }


    //------------------------------------------------------------------------
    //  версия прошивки узла

    public short getNodeVersion() {
        return nodeVersion;
    }

    public void setNodeVersion(short nodeVersion) {
        this.nodeVersion = nodeVersion;
    }


    //------------------------------------------------------------------------
    //  группы управления

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
    //  реакция на события

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



    //##############################################################################################################
    //######    методы класса-


    //------------------------------------------------------------------------
    //  получение списка параметров узла в виде строки

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
    //  получение полной информации об узле в виде строки

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
                getNodeId(),
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
    //  получение краткой информации об узле в виде строки

    @Override
    public String toString() {
        String result = String.format("{\"id\":\"%d\",\"label\":\"%s\",\"location\":\"%s\",\"type\":\"%s\"}",
                getNodeId(),
                getNodeName(),
                getNodeLocation(),
                getNodeType()
        );

        return result;
    }

}
