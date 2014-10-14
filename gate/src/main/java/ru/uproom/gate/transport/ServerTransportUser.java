package ru.uproom.gate.transport;

/**
 * Marker interface for objects which must use a transport
 * <p/>
 * Created by osipenko on 09.09.14.
 */
public interface ServerTransportUser {
    public void setTransport(ServerTransportMarker transport);

    public void setLink(boolean link);
}
