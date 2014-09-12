package ru.uproom.gate.zwave;

/**
 * Created by osipenko on 14.08.14.
 */
public class ZWaveValueIndexFactory {

    public static int createIndex(short commandClass, short instance, short index) {

        return (commandClass << 16) | (instance << 8) | index;

    }

}
