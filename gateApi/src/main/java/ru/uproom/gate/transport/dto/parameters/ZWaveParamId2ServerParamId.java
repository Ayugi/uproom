package ru.uproom.gate.transport.dto.parameters;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by osipenko on 14.09.14.
 */
public class ZWaveParamId2ServerParamId {

    // mapping Server Parameter Name to Value Index
    private static final Map<DeviceParametersNames, Integer> names =
            new EnumMap<DeviceParametersNames, Integer>(DeviceParametersNames.class) {{
                // switch
                put(DeviceParametersNames.Unknown, 0);
                put(DeviceParametersNames.State, 0);
                put(DeviceParametersNames.ApplicationVersion, 8782082);
                put(DeviceParametersNames.ProtocolVersion, 8782081);
                put(DeviceParametersNames.Switch, 2425088);
                put(DeviceParametersNames.Level, 2490624);
                put(DeviceParametersNames.StartLevel, 2490628);
                put(DeviceParametersNames.Energy, 3277056);
                put(DeviceParametersNames.Power, 3277064);
            }};

    public static DeviceParametersNames getServerParamId(int index) {
        DeviceParametersNames result = DeviceParametersNames.Unknown;
        for (DeviceParametersNames name : DeviceParametersNames.values()) {
            if (names.get(name) == index) {
                result = name;
                break;
            }
        }
        return result;
    }

    public static int getZWaveParamId(DeviceParametersNames name) {
        return names.get(name);
    }

}
