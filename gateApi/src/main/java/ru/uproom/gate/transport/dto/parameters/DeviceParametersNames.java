package ru.uproom.gate.transport.dto.parameters;

/**
 * contains names of device parameters which not included in z-wave conception
 * <p/>
 * Created by osipenko on 18.09.14.
 */
public enum DeviceParametersNames {
    Unknown(0),
    ServerDeviceId(1),
    GateDeviceId(2),
    ServerDeviceType(1),
    State(3),
    ApplicationVersion(8782082),
    ProtocolVersion(8782081),
    Switch(2425088),
    Level(2490624),
    StartLevel(2490628),
    Energy(3277056),
    Power(3277064);

    private int zwaveCode;

    DeviceParametersNames(int zwaveCode) {
        this.zwaveCode = zwaveCode;
    }

    public static DeviceParametersNames byZWaveCode(int zwaveCode) {
        for (DeviceParametersNames name : values()) {
            if (zwaveCode == name.getZwaveCode()) return name;
        }
        return Unknown;
    }

    public int getZwaveCode() {
        return zwaveCode;
    }
}
