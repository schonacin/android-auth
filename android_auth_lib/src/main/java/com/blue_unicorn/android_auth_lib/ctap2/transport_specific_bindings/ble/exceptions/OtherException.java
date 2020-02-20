package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Error;

public class OtherException extends BleException {

    public OtherException(String message) {
        super(Error.ERR_OTHER, message);
    }

    public OtherException(String message, Throwable cause) {
        super(Error.ERR_OTHER, message, cause);
    }
}
