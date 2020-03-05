package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants;

import java.util.Arrays;
import java.util.List;

/*
 * Represents command constants as described in <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-constants">.
 */
public abstract class Command {

    private static byte PING = (byte) 0x81;
    private static byte KEEPALIVE = (byte) 0x82;
    private static byte MSG = (byte) 0x83;
    private static byte CANCEL = (byte) 0xbe;
    private static byte ERROR = (byte) 0xbf;

    public static List<Byte> commands = Arrays.asList(PING, KEEPALIVE, MSG, CANCEL, ERROR);
}
