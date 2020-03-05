package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants;

/*
 * Represents error constants as described in <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-constants">corresponding section of the CTAP2 specification</>.
 */
public abstract class Error {

    public static final byte ERR_INVALID_CMD = (byte) 0x01;
    public static final byte ERR_INVALID_PAR = (byte) 0x02;
    public static final byte ERR_INVALID_LEN = (byte) 0x03;
    public static final byte ERR_INVALID_SEQ = (byte) 0x04;
    public static final byte ERR_REQ_TIMEOUT = (byte) 0x05;
    public static final byte ERR_BUSY = (byte) 0x06;
    public static final byte ERR_OTHER = (byte) 0x7f;
}
