package ru.uproom.gate.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.transport.domain.DelayTimer;

/**
 * Created by osipenko on 22.12.14.
 */
public class ServerTransportWatchDog implements Runnable {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(ServerTransportWatchDog.class);

    private boolean watchDogWork = true;
    private boolean isWatchDogOn;
    private GateDevicesSet home;
    private int watchDogId;
    private int watchDogCounter;
    private long periodWaitPing;


    //##############################################################################################################
    //######    constructors


    public ServerTransportWatchDog(GateDevicesSet home, int watchDogId, long periodWaitPing) {
        this.home = home;
        this.watchDogId = watchDogId;
        this.periodWaitPing = periodWaitPing;
    }


    //##############################################################################################################
    //######    getters and setters


    //------------------------------------------------------------------------
    //  change this counter for watch dog not restart link

    public void incWatchDogCounter() {
        watchDogCounter++;
        if (watchDogCounter > 999999) watchDogCounter = 0;
    }


    //------------------------------------------------------------------------
    //  activate watch dog

    public void setWatchDogOn(boolean isWatchDogOn) {
        // log information
        if (isWatchDogOn && !this.isWatchDogOn)
            LOG.info("watchdog id : {} - gate have a ping command from server - LINK SET ON", new Object[]{
                    watchDogId
            });
        else if (!isWatchDogOn && this.isWatchDogOn)
            LOG.error("watchdog id : {} - gate have not a ping command from server - LINK SET OFF", new Object[]{
                    watchDogId
            });
        // set WatchDog flag
        this.isWatchDogOn = isWatchDogOn;
    }


    //##############################################################################################################
    //######    inner classes


    //##############################################################################################################
    //######    methods


    public void stop() {
        watchDogWork = false;
    }

    @Override
    public void run() {

        int watchDogCounterPrevious = -1;

        while (watchDogWork) {
            if (isWatchDogOn && watchDogCounter == watchDogCounterPrevious) {
                setWatchDogOn(false);
                //home.getTransport().restartLink(watchDogId);
            }
            watchDogCounterPrevious = watchDogCounter;
            DelayTimer.sleep(periodWaitPing);
        }

    }
}
