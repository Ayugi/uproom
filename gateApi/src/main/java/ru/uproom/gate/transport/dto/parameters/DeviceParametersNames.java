package ru.uproom.gate.transport.dto.parameters;

/**
 * contains names of device parameters which not included in z-wave conception
 * <p/>
 * Created by osipenko on 18.09.14.
 */
public enum DeviceParametersNames {
    // gate parameters
    Unknown(0, true),
    ServerDeviceType(1, true),
    State(2, false),
    ManufacturerName(3, true),
    ManufacturerId(4, true),
    ProductName(5, true),
    ProductId(6, true),
    ProductType(7, true),
    // z-wave parameters
    Basic(2097408, true),
    Switch(2425088, false),
    Level(2490624, false),
    Bright(2490625, true),
    Dim(2490626, false),
    IgnoreStartLevel(2490627, false),
    StartLevel(2490628, false),
    SwitchAll(2556160, true),
    Energy(3277056, true),
    PreviousReading(3277057, true),
    Interval(3277058, false),
    Power(3277064, true),
    Exporting(3277088, true),
    Reset(3277089, false),
    TruePeriod(7340289, true),
    SendOutBasicCommand(7340290, true),
    MeterReportPeriod(7340291, false),
    LibraryVersion(8782080, true),
    ProtocolVersion(8782081, true),
    ApplicationVersion(8782082, true);

    private int zwaveCode;
    private boolean readOnly;

    DeviceParametersNames(int zwaveCode, boolean readOnly) {
        this.zwaveCode = zwaveCode;
        this.readOnly = readOnly;
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

    public boolean isReadOnly() {
        return readOnly;
    }
}
