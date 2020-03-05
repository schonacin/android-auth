package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants;

/*
 * Represents timeout constants as described in <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-command-completion">.
 */
public abstract class Timeout {

    public static int kMaxCommandTransmitDelayMillis = 1500;
    public static int kErrorWaitMillis = 2000;
    public static int kKeepAliveMillis = 500;
}
