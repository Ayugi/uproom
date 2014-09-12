package ru.uproom.gate.transport.command;

/**
 * Created by HEDIN on 28.08.2014.
 */
public enum CommandType {
    AddDevice,
    RemoveDevice,
    Cancel,
    GetDeviceList,
    SendDeviceList,
    Handshake,
    SetDeviceParameter
}
