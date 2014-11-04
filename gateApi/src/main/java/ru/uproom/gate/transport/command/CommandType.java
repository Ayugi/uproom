package ru.uproom.gate.transport.command;

/**
 * Created by HEDIN on 28.08.2014.
 */
public enum CommandType {
    AddModeOn,
    RemoveModeOn,
    Cancel,
    GetDeviceList,
    SendDeviceList,
    Handshake,
    SetDeviceParameter,
    NetworkControllerState,
    Shutdown,
    Ping
}
