package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants;

import java.util.Arrays;
import java.util.List;

/*
 * Represents status constants of the keepalive command as described in <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-constants">corresponding section of the CTAP2 specification</>.
 */
public abstract class Keepalive {

    public static final byte PROCESSING = (byte) 0x01;
    public static final byte UP_NEEDED = (byte) 0x02;

    public static final List<Byte> status = Arrays.asList(PROCESSING, UP_NEEDED);
}
