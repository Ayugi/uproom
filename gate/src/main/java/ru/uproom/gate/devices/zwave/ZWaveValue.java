package ru.uproom.gate.devices.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwave4j.Manager;
import org.zwave4j.ValueId;
import ru.uproom.gate.notifications.zwave.NotificationWatcherImpl;
import ru.uproom.gate.transport.domain.DelayTimer;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by osipenko on 14.08.14.
 */
public class ZWaveValue {


    //=============================================================================================================
    //======    fields

    private static final Logger LOG = LoggerFactory.getLogger(NotificationWatcherImpl.class);

    private int id;
    private ValueId valueId;
    private DeviceParametersNames valueName;
    private boolean readOnly;

    private ZWaveValueSetLevel setLevel;
    private Thread threadSetLevel;


    //=============================================================================================================
    //======    constructors


    public ZWaveValue(ValueId valueId) {
        this.valueId = valueId;
        this.id = ZWaveValueIndexFactory.createIndex(valueId);
        this.valueName = DeviceParametersNames.byZWaveCode(this.id);
        this.readOnly = Manager.get().isValueReadOnly(valueId);
    }


    //=============================================================================================================
    //======    getters & setters


    //------------------------------------------------------------------------
    //  z-wave value label

    public String getValueLabel() {
        return Manager.get().getValueLabel(valueId);
    }


    //------------------------------------------------------------------------
    //  gate value id

    public int getId() {
        return id;
    }


    //=============================================================================================================
    //======    inner classes


    //------------------------------------------------------------------------
    //  change value "level" smoothing

    private Object getValue() {
        switch (valueId.getType()) {
            case BOOL:
                AtomicReference<Boolean> b = new AtomicReference<>();
                Manager.get().getValueAsBool(valueId, b);
                return b.get();
            case BYTE:
                AtomicReference<Short> bb = new AtomicReference<>();
                Manager.get().getValueAsByte(valueId, bb);
                return bb.get();
            case DECIMAL:
                AtomicReference<Float> f = new AtomicReference<>();
                Manager.get().getValueAsFloat(valueId, f);
                return f.get();
            case INT:
                AtomicReference<Integer> i = new AtomicReference<>();
                Manager.get().getValueAsInt(valueId, i);
                return i.get();
            case LIST:
                return null;
            case SCHEDULE:
                return null;
            case SHORT:
                AtomicReference<Short> s = new AtomicReference<>();
                Manager.get().getValueAsShort(valueId, s);
                return s.get();
            case STRING:
                AtomicReference<String> ss = new AtomicReference<>();
                Manager.get().getValueAsString(valueId, ss);
                return ss.get();
            case BUTTON:
                return null;
            case RAW:
                AtomicReference<short[]> sss = new AtomicReference<>();
                Manager.get().getValueAsRaw(valueId, sss);
                return sss.get();
            default:
                return null;
        }
    }


    //=============================================================================================================
    //======    methods


    //------------------------------------------------------------------------
    //  get parameter value

    public String getValueAsString() {
        Object obj = getValue();
        return (obj == null) ? "null" : obj.toString();
    }


    //------------------------------------------------------------------------
    //  get parameter value as string

    @Override
    public String toString() {

        return String.format("{\"id\":\"%d\",\"label\":\"%s\",\"value\":\"%s\"}",
                id,
                getValueLabel(),
                getValueAsString()
        );
    }


    //------------------------------------------------------------------------
    //  parameter information as string

    private boolean setLevelInit(String value) {

        // stop previous instance
        if (setLevel != null) setLevel.setWork(false);
        // create new instance
        setLevel = new ZWaveValueSetLevel(Integer.parseInt(value));
        threadSetLevel = new Thread(setLevel);
        threadSetLevel.start();

        return true;
    }


    //------------------------------------------------------------------------
    //  initialize method for smoothing set value "level"

    public boolean setValue(String value) {

        if (this.readOnly) return false;
        if (getValueAsString().equalsIgnoreCase(value)) return false;

        boolean result = false;

        if (valueName == DeviceParametersNames.Level) {
            // range of level are number 0-99 and 255
            int iValue = Integer.parseInt(value);
            if (iValue <= 0) value = "0";
            else if (iValue >= 99) value = "99";
            // smoothing set for level
            result = setLevelInit(value);
            LOG.debug(">>>> set level : " + this.toString() + " to value : " + value);

        } else
            result = Manager.get().setValueAsString(valueId, value);

        return result;
    }


    //------------------------------------------------------------------------
    //  set parameter value as string

    public class ZWaveValueSetLevel implements Runnable {

        private boolean work = true;
        private int setLevelJitter = 2;
        private int setLevelJitterTime = 100; //ms
        private int level;

        ZWaveValueSetLevel(int level) {
            this.level = level;
        }

        public void setWork(boolean work) {
            this.work = work;
        }

        @Override
        public void run() {

            int value = Integer.parseInt(getValueAsString());
            int multiplier = 1;
            if (value > level) multiplier = -1;

            while (work && (level - value) * multiplier > 0) {
                Manager.get().setValueAsString(valueId, String.format("%d", value));
                value += (setLevelJitter * multiplier);
                DelayTimer.sleep(setLevelJitterTime);
            }

        }

    }
}
