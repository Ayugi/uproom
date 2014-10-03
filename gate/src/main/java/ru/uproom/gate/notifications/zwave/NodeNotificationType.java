package ru.uproom.gate.notifications.zwave;

/**
 * Created by osipenko on 29.09.14.
 */
public enum NodeNotificationType {
    MSG_COMPLETE(0),
    TIMEOUT(1),
    NO_OPERATION(2),
    AWAKE(3),
    SLEEP(4),
    DEAD(5),
    ALIVE(6);

    private int code;

    NodeNotificationType(int code) {
        this.code = code;
    }

    public static NodeNotificationType byCode(int code) {
        for (NodeNotificationType name : values()) {
            if (code == name.getCode()) return name;
        }
        return MSG_COMPLETE;
    }

    public int getCode() {
        return code;
    }
}
