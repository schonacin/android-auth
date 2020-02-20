package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Error;

public class AuthenticatorBusyException extends BleException {

    public AuthenticatorBusyException(String message) {
        super(Error.ERR_BUSY, message);
    }

    public AuthenticatorBusyException(String message, Throwable cause) {
        super(Error.ERR_BUSY, message, cause);
    }
}
