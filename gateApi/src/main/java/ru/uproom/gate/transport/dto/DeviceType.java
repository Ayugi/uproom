package ru.uproom.gate.transport.dto;

import java.util.Arrays;
import java.util.List;

/**
 * map of device type for server
 * <p/>
 * Created by osipenko on 09.09.14.
 */
public enum DeviceType {

    None(
            "Non Interoperable"
    ),

    LowControl(
            "Semi Interoperable",
            "Energy Production"
    ),

    Controller(
            "Remote Controller",
            "Portable Remote Controller",
            "Portable Scene Controller",
            "Portable Installer Tool",
            "Static Controller",
            "Static PC Controller",
            "Static Scene Controller",
            "Static Installer Tool"
    ),

    ControlPoint(
            "AV Control Point",
            "Satellite Receiver",
            "Satellite Receiver V2",
            "Doorbell"
    ),

    Display(
            "Display",
            "Simple Display"
    ),

    Thermostat(
            "Thermostat",
            "General Thermostat",
            "Heating Thermostat",
            "Setback Schedule Thermostat",
            "Setpoint Thermostat",
            "Setback Thermostat",
            "General Thermostat V2"
    ),

    WindowCovering(
            "Window Covering",
            "Simple Window Covering"
    ),

    RepeaterSlave(
            "Repeater Slave",
            "Basic Repeater Slave"
    ),

    BinarySwitch(
            "Binary Switch",
            "Binary Power Switch",
            "Binary Scene Switch",
            "Binary Remote Switch",
            "Binary Toggle Switch",
            "Binary Toggle Remote Switch"
    ),

    MultilevelSwitch(
            "Multilevel Switch",
            "Multilevel Power Switch",
            "Multiposition Motor",
            "Multilevel Scene Switch",
            "Motor Control Class A",
            "Motor Control Class B",
            "Motor Control Class C",
            "Multilevel Remote Switch",
            "Multilevel Toggle Switch",
            "Multilevel Toggle Remote Switch"
    ),

    // Z-IP System
    ZipGateway(
            "Z/IP Gateway",
            "Z/IP Tunneling Gateway",
            "Z/IP Advanced Gateway"
    ),
    ZipNode(
            "Z/IP Node",
            "Z/IP Tunneling Node",
            "Z/IP Advanced Node"
    ),

    Ventilation(
            "Ventilation",
            "Residential Heat Recovery Ventilation"
    ),

    BinarySensor(
            "Binary Sensor",
            "Routing Binary Sensor"
    ),

    MultilevelSensor(
            "Multilevel Sensor",
            "Routing Multilevel Sensor"
    ),

    PulseMeter(
            "Pulse Meter"
    ),

    Meter(
            "Meter",
            "Simple Meter"
    ),

    EntryControl(
            "Entry Control",
            "Door Lock",
            "Advanced Door Lock",
            "Secure Keypad Door Lock"
    ),

    AlarmSensor(
            "Alarm Sensor",
            "Basic Routing Alarm Sensor",
            "Routing Alarm Sensor",
            "Basic Zensor Alarm Sensor",
            "Zensor Alarm Sensor",
            "Advanced Zensor Alarm Sensor",
            "Basic Routing Smoke Sensor",
            "Routing Smoke Sensor",
            "Basic Zensor Smoke Sensor",
            "Zensor Smoke Sensor",
            "Advanced Zensor Smoke Sensor"
    );

    private List<String> stringKeys;

    DeviceType(String... stringKeys) {
        this.stringKeys = Arrays.asList(stringKeys);
    }

    public static DeviceType byStringKey(String stringKey) {
        for (DeviceType value : values()) {
            if (value.getStringKeys().indexOf(stringKey) >= 0) return value;
        }
        return None;
    }

    public List<String> getStringKeys() {
        return stringKeys;
    }
}
