package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants;

import java.util.Arrays;
import java.util.List;

/*
 * Represents command constants as described in <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-constants">corresponding section of the CTAP2 specification</>.
 */
public abstract class Command {

    private static final byte PING = (byte) 0x81;
    private static final byte KEEPALIVE = (byte) 0x82;
    private static final byte MSG = (byte) 0x83;
    private static final byte CANCEL = (byte) 0xbe;
    private static final byte ERROR = (byte) 0xbf;

    public static final List<Byte> commands = Arrays.asList(PING, KEEPALIVE, MSG, CANCEL, ERROR);
}
