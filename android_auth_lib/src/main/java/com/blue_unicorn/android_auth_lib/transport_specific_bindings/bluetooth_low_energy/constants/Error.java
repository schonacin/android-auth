package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants;

public abstract class Error {

    public static byte ERR_INVALID_CMD = (byte) 0x01;
    public static byte ERR_INVALID_PAR = (byte) 0x02;
    public static byte ERR_INVALID_LEN = (byte) 0x03;
    public static byte ERR_INVALID_SEQ = (byte) 0x04;
    public static byte ERR_REQ_TIMEOUT = (byte) 0x05;
    public static byte ERR_BUSY = (byte) 0x06;
    public static byte ERR_OTHER = (byte) 0x7f;
}
