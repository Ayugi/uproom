package ru.uproom.gate.zwave;

import org.zwave4j.Manager;
import org.zwave4j.ValueId;
import ru.uproom.gate.transport.dto.parameters.DeviceParametersNames;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by osipenko on 14.08.14.
 */
public class ZWaveValue {


    //=============================================================================================================
    //======    fields


    private int id;
    private ValueId valueId;
    private DeviceParametersNames valueName;
    private ArrayList<ZWaveValueCallback> events = new ArrayList<ZWaveValueCallback>();


    //=============================================================================================================
    //======    constructors


    public ZWaveValue(ValueId valueId) {
        this.valueId = valueId;
        String label = Manager.get().getValueLabel(valueId);
        this.id = ZWaveValueIndexFactory.createIndex(valueId);
        this.valueName = DeviceParametersNames.byZWaveCode(id);
    }


    //=============================================================================================================
    //======    getters & setters


    //------------------------------------------------------------------------
    //  z-wave value ID

    public ValueId getValueId() {
        return valueId;
    }

    public void setValueId(ValueId valueId) {
        this.valueId = valueId;
    }


    //------------------------------------------------------------------------
    //  z-wave value label

    public String getValueLabel() {
        return Manager.get().getValueLabel(valueId);
    }


    //------------------------------------------------------------------------
    //  z-wave value index

    public Short getValueIndex() {
        return valueId.getIndex();
    }


    //------------------------------------------------------------------------
    //  gate value id

    public int getId() {
        return id;
    }


    //------------------------------------------------------------------------
    //  z-wave value command class

    public Short getValueCommandClass() {
        return valueId.getCommandClassId();
    }


    //------------------------------------------------------------------------
    //  z-wave value instance id

    public Short getValueInstance() {
        return valueId.getInstance();
    }


    //------------------------------------------------------------------------
    //  events associated with value

    public boolean addEvent(ZWaveValueCallback event) {
        return events.add(event);
    }

    public boolean removeEvent(ZWaveValueCallback event) {
        return events.remove(event);
    }

    public void callEvents() {
        for (ZWaveValueCallback event : events) {
            event.onCallback(this);
        }
    }


    //------------------------------------------------------------------------
    //  server value name

    public DeviceParametersNames getValueName() {
        return valueName;
    }


    //=============================================================================================================
    //======    methods


    //------------------------------------------------------------------------
    //  get parameter value

    private Object getValue() {
        switch (valueId.getType()) {
            case BOOL:
                AtomicReference<Boolean> b = new AtomicReference<Boolean>();
                Manager.get().getValueAsBool(valueId, b);
                return b.get();
            case BYTE:
                AtomicReference<Short> bb = new AtomicReference<Short>();
                Manager.get().getValueAsByte(valueId, bb);
                return bb.get();
            case DECIMAL:
                AtomicReference<Float> f = new AtomicReference<Float>();
                Manager.get().getValueAsFloat(valueId, f);
                return f.get();
            case INT:
                AtomicReference<Integer> i = new AtomicReference<Integer>();
                Manager.get().getValueAsInt(valueId, i);
                return i.get();
            case LIST:
                return null;
            case SCHEDULE:
                return null;
            case SHORT:
                AtomicReference<Short> s = new AtomicReference<Short>();
                Manager.get().getValueAsShort(valueId, s);
                return s.get();
            case STRING:
                AtomicReference<String> ss = new AtomicReference<String>();
                Manager.get().getValueAsString(valueId, ss);
                return ss.get();
            case BUTTON:
                return null;
            case RAW:
                AtomicReference<short[]> sss = new AtomicReference<short[]>();
                Manager.get().getValueAsRaw(valueId, sss);
                return sss.get();
            default:
                return null;
        }
    }


    //------------------------------------------------------------------------
    //  get parameter value as string

    public String getValueAsString() {
        Object obj = getValue();
        return (obj == null) ? "null" : obj.toString();
    }


    //------------------------------------------------------------------------
    //  parameter information as string

    @Override
    public String toString() {

        String result = String.format("{\"id\":\"%d\",\"label\":\"%s\",\"value\":\"%s\"}",
                id,
                getValueLabel(),
                getValueAsString()
        );

        return result;
    }


    //------------------------------------------------------------------------
    //  set parameter value as string

    public boolean setValue(String value) {
        return Manager.get().setValueAsString(valueId, value);
    }
}
