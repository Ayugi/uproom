package ru.uproom.gate.devices.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zwave4j.*;
import ru.uproom.gate.devices.GateDevicesSet;
import ru.uproom.gate.domain.DelayTimer;
import ru.uproom.gate.transport.ServerTransport;
import ru.uproom.gate.transport.command.NetworkControllerStateCommand;
import ru.uproom.gate.transport.command.PingCommand;
import ru.uproom.gate.transport.command.SendDeviceListCommand;
import ru.uproom.gate.transport.command.SetDeviceParameterCommand;
import ru.uproom.gate.transport.dto.DeviceDTO;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;
import ru.uproom.gate.transport.dto.parameters.DeviceStateEnum;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * inner class which contain list of Z-Wave devices
 * <p/>
 * Created by osipenko on 10.08.14.
 */
@Service
public class ZWaveHome implements GateDevicesSet {


    //##############################################################################################################
    //######    fields

    private static final Logger LOG = LoggerFactory.getLogger(GateDevicesSet.class);
    private final Map<Integer, ZWaveNode> nodes = new HashMap<>();
    @Value("${zwave_stick}")
    private String zWaveStick;
    @Value("${zwave_cfg_path}")
    private String zWaveCfgPath;
    @Value("${zwave_usr_path}")
    private String zWaveUserPath;
    private Thread threadDriver;
    private long homeId;
    private boolean ready;
    private boolean failed;
    @Autowired
    private ServerTransport transport;
    @Autowired
    private NotificationWatcher watcher;
    @Autowired
    private ControllerCallback controllerCallback;

    private DeviceStateEnum requestState;

    private ServerTransportWatchDog watchdog;
    private Thread threadWatchdog;
    private int watchDogCounter;
    @Value("${period_wait_ping}")
    private int periodWaitPing;


    //##############################################################################################################
    //######    constructors


    public ZWaveHome() {
        // loading openZWave library
        LOG.info("Libraries loading ...");
        NativeLibraryLoader.loadLibrary(ZWave4j.LIBRARY_NAME, ZWave4j.class);
        LOG.info("Libraries loaded");

    }

    @PostConstruct
    public void init() {

        // reading current librarian options
        LOG.info("Options loading ...");
        final Options options = Options.create(
                zWaveCfgPath,
                zWaveUserPath,
                ""
        );
        options.addOptionBool("ConsoleOutput", false);
        options.lock();
        LOG.info("Options loaded");

        // create manager
        Manager.create();
        Manager.get().addWatcher(watcher, this);
        startDriver();

        startWatchDog();
    }

    @PreDestroy
    public void close() {
        threadDriver.interrupt();
        watchdog.setWatchDogWork(false);
        Manager.get().removeWatcher(watcher, this);
        Manager.get().removeDriver(zWaveStick);
        Manager.destroy();
        Options.destroy();
    }


    //##############################################################################################################
    //######    getters and setters


    //------------------------------------------------------------------------
    //  home ID

    public long getHomeId() {
        return homeId;
    }

    public void setHomeId(long homeId) {
        this.homeId = homeId;
    }


    //------------------------------------------------------------------------
    //  z-wave system ready to work

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        getDeviceDTOList();
    }

    public boolean isFailed() {
        boolean temp = failed;
        failed = false;
        return temp;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }


    //------------------------------------------------------------------------
    // node list

    @Override
    public void addGateDevice(int index) {
        ZWaveNode node = new ZWaveNode(this, index);
        nodes.put(index, node);
        if (!isReady()) return;
        transport.sendCommand(new SetDeviceParameterCommand(node.getDeviceDTO()));
    }

    @Override
    public void removeGateDevice(int index) {
        ZWaveNode node = nodes.remove(index);
        node.setZId(0);
        transport.sendCommand(new SetDeviceParameterCommand(node.getDeviceDTO()));
    }


    //------------------------------------------------------------------------
    // node groups

    @Override
    public void addDeviceGroup(int indexDevice, int indexGroup) {
        ZWaveNode node = nodes.get(indexDevice);
        if (node == null) return;
        node.setGroup(indexGroup);
    }


    //------------------------------------------------------------------------
    // device parameters

    @Override
    public void setGateDeviceParameter(int indexDevice, DeviceParametersNames paramName, Object paramValue) {
        ZWaveNode node = nodes.get(indexDevice);
        if (node == null) return;
        if (paramValue instanceof ValueId) paramValue = new ZWaveValue((ValueId) paramValue);
        node.setParameter(paramName, paramValue);
        if (!isReady()) return;
        transport.sendCommand(new SetDeviceParameterCommand(
                node.getDeviceParameters(paramName)
        ));
    }

    @Override
    public void removeGateDeviceParameter(int indexDevice, DeviceParametersNames paramName) {
        ZWaveNode node = nodes.get(indexDevice);
        if (node == null) return;
        node.removeParameter(paramName);
    }


    //------------------------------------------------------------------------
    // changing devices set mode will activate

    @Override
    public void requestMode(DeviceStateEnum mode) {
        switch (mode) {
            case Add:
                requestState = DeviceStateEnum.Add;
                Manager.get().beginControllerCommand(getHomeId(), ControllerCommand.ADD_DEVICE, controllerCallback);
                break;
            case Remove:
                requestState = DeviceStateEnum.Remove;
                Manager.get().beginControllerCommand(getHomeId(), ControllerCommand.REMOVE_DEVICE, controllerCallback);
                break;
            case Cancel:
                requestState = DeviceStateEnum.Cancel;
                Manager.get().cancelControllerCommand(getHomeId());
                break;
            default:
                requestState = DeviceStateEnum.Unknown;
        }
    }

    @Override
    public DeviceStateEnum getRequestedMode() {
        return requestState;
    }


    //------------------------------------------------------------------------
    // state of Z-Wave Network Controller

    @Override
    public DeviceStateEnum getControllerState() {

        DeviceStateEnum state = DeviceStateEnum.Down;
        if (homeId <= 0) return state;

        Integer index = (int) Manager.get().getControllerNodeId(homeId);
        ZWaveNode node = nodes.get(index);

        if (node != null) {
            Object o = node.getParameters().get(DeviceParametersNames.State);
            if (o != null && o instanceof DeviceStateEnum) state = (DeviceStateEnum) o;
        }

        return state;
    }

    @Override
    public void setControllerState(DeviceStateEnum state, boolean clearRequest) {

        ZWaveNode node = null;
        if (homeId > 0) {
            Integer index = (int) Manager.get().getControllerNodeId(homeId);
            node = nodes.get(index);
        }

        if (node != null) {
            node.getParameters().put(DeviceParametersNames.State, state);
        }

        if (clearRequest) requestState = DeviceStateEnum.Unknown;
        transport.sendCommand(new NetworkControllerStateCommand(state));
    }


    //##############################################################################################################
    //######    inner classes

    private void startDriver() {
        ZWaveHomeDriver driver = new ZWaveHomeDriver();
        threadDriver = new Thread(driver);
        threadDriver.start();
    }


    //##############################################################################################################
    //######    methods


    //------------------------------------------------------------------------
    // create Z-Wave driver and keep it work

    public List<DeviceDTO> getDeviceDTOList() {
        List<DeviceDTO> devices = new ArrayList<>();
        for (Map.Entry<Integer, ZWaveNode> entry : nodes.entrySet()) {
            devices.add(entry.getValue().getDeviceDTO());
        }
        transport.sendCommand(new SendDeviceListCommand(devices));
        return devices;
    }


    //------------------------------------------------------------------------
    //  find node by ServerID

    @Override
    public void setDeviceDTO(DeviceDTO dto) {
        ZWaveNode node = nodes.get(dto.getZId());
        if (node == null) return;
        node.setParams(dto);
    }


    //------------------------------------------------------------------------
    //  create device list as list of serializable objects

    @Override
    public String toString() {
        return String.format("{\"id\":\"%d\"", homeId);
    }


    //------------------------------------------------------------------------
    //  gate receive ping from server

    @Override
    public void ping() {

        watchdog.setWatchDogOn(true);
        watchDogCounter++;
        if (watchDogCounter > 99999) watchDogCounter = 0;
        transport.sendCommand(new PingCommand());

    }


    //------------------------------------------------------------------------
    //  listener for check data exchange

    private void startWatchDog() {
        watchdog = new ServerTransportWatchDog();
        threadWatchdog = new Thread(watchdog);
        threadWatchdog.start();
    }


    //------------------------------------------------------------------------
    //  listener for check data exchange

    public class ZWaveHomeDriver implements Runnable {

        @Override
        public void run() {
            Manager.get().addDriver(zWaveStick);
            while (!isFailed() && !Thread.currentThread().isInterrupted()) DelayTimer.sleep(100);
            Manager.get().removeDriver(zWaveStick);
            DelayTimer.sleep(5000);
            startDriver();
        }
    }


    //------------------------------------------------------------------------
    //  check data exchange between gate and server

    public class ServerTransportWatchDog implements Runnable {

        private boolean watchDogWork = true;
        private boolean isWatchDogOn;

        public void setWatchDogWork(boolean watchDogWork) {
            this.watchDogWork = watchDogWork;
        }

        public void setWatchDogOn(boolean isWatchDogOn) {
            this.isWatchDogOn = isWatchDogOn;
        }

        @Override
        public void run() {

            int watchDogCounterPrevious = -1;

            while (watchDogWork) {
                if (isWatchDogOn && watchDogCounter == watchDogCounterPrevious) {
                    isWatchDogOn = false;
                    transport.restartLink();
                }
                watchDogCounterPrevious = watchDogCounter;
                DelayTimer.sleep(periodWaitPing);
            }

        }
    }


}
