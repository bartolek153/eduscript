package org.eduscript.datastructures;

public class Trigger {

    private TriggerType type;
    private String value;

    public Trigger() {
    }

    public Trigger(TriggerType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TriggerType getType() {
        return type;
    }

    public void setType(TriggerType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
