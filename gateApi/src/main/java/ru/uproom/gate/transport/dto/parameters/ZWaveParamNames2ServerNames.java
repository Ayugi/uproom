package ru.uproom.gate.transport.dto.parameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by osipenko on 14.09.14.
 */
public class ZWaveParamNames2ServerNames {

    private static final Map<String, String> names = new HashMap<String, String>() {{
        // switch
        put("Switch", "switch");
    }};

    public static String getServerName(String zwaveName) {
        String name = names.get(zwaveName);
        return (name == null) ? "" : name;
    }

}
