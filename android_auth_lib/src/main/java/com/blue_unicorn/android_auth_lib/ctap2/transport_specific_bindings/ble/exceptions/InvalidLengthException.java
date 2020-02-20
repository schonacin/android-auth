package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Error;

public class InvalidLengthException extends BleException {

    public InvalidLengthException(String message) {
        super(Error.ERR_INVALID_LEN, message);
    }

    public InvalidLengthException(String message, Throwable cause) {
        super(Error.ERR_INVALID_LEN, message, cause);
    }
}
