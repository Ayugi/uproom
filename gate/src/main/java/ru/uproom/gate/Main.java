package ru.uproom.gate;

import org.zwave4j.Manager;
import org.zwave4j.NativeLibraryLoader;
import org.zwave4j.Options;
import org.zwave4j.ZWave4j;

/**
 * Created by osipenko on 27.07.14.
 */
public class Main {
    public static void main(String[] args) {
        System.out.print("hello");
        NativeLibraryLoader.loadLibrary(ZWave4j.LIBRARY_NAME, ZWave4j.class);

        final Options options = Options.create("a", "", "");
        options.addOptionBool("ConsoleOutput", false);
        options.lock();

        Manager manager = Manager.create();


        manager.addWatcher(new MainWatcher(), null);

    }
}
