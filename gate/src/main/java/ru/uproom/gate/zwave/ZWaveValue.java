package ru.uproom.gate.zwave;

import org.zwave4j.Manager;
import org.zwave4j.ValueId;
import ru.uproom.gate.transport.dto.parameters.ZWaveParamNames2ServerNames;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by osipenko on 14.08.14.
 */
public class ZWaveValue {


    //=============================================================================================================
    //======    fields


    private ValueId valueId = null;
    private String valueName;
    private ArrayList<ZWaveValueCallback> events = new ArrayList<ZWaveValueCallback>();


    //=============================================================================================================
    //======    constructors


    public ZWaveValue(ValueId valueId) {
        this.valueId = valueId;
        String label = Manager.get().getValueLabel(valueId);
        this.valueName = ZWaveParamNames2ServerNames.getServerName(label);
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
    //  z-wave value command class

    public Short getValueCommandClass() {
        return valueId.getCommandClassId();
    }


    //------------------------------------------------------------------------
    //  z-wave value instance index

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

    public String getValueName() {
        return valueName;
    }


    //=============================================================================================================
    //======    методы класса


    //------------------------------------------------------------------------
    //  получение значения параметра

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
    //  получение значения параметра в виде строки

    public String getValueAsString() {
        Object obj = getValue();
        return (obj == null) ? "null" : obj.toString();
    }


    //------------------------------------------------------------------------
    //  информация о параметре в виде строки

    @Override
    public String toString() {

        Integer index = ZWaveValueIndexFactory.createIndex(
                valueId.getCommandClassId(),
                valueId.getInstance(),
                valueId.getIndex()
        );
        String result = String.format("{\"id\":\"%d\",\"label\":\"%s\",\"value\":\"%s\"}",
                index,
                getValueLabel(),
                getValueAsString()
        );

        return result;
    }
}
