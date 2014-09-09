package ru.uproom.gate;

/**
 * Created by osipenko on 02.09.14.
 */
public class DelayTimer {

    public static void sleep(long msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
