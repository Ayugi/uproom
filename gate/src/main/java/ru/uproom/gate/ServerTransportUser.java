package ru.uproom.gate;

/**
 * Marker interface for objects which must use a transport
 * <p/>
 * Created by osipenko on 09.09.14.
 */
public interface ServerTransportUser {
    public void setTransport(ServerTransport transport);
}
