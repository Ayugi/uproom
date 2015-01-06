package ru.uproom.gate.transport.command;

/**
 * Created by HEDIN on 28.08.2014.
 */
public enum CommandType {
    None,
    AddModeOn,
    RemoveModeOn,
    Cancel,
    GetDeviceList,
    SendDeviceList,
    Handshake,
    SetDeviceParameter,
    NetworkControllerState,
    Shutdown,
    Ping,
    Exit,
    Help
}
