package ru.uproom.gate.devices.zwave;

import org.zwave4j.ValueId;

/**
 * Created by osipenko on 14.08.14.
 */
public class ZWaveValueIndexFactory {

    public static int createIndex(ValueId valueId) {
        int index = ((int) valueId.getCommandClassId()) << 16;
        index |= ((int) valueId.getInstance()) << 8;
        index |= (int) valueId.getIndex();
        return index;
    }

}
