package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Error;

public class InvalidCommandException extends BluetoothLowEnergyException {

    public InvalidCommandException(String message) {
        super(Error.ERR_INVALID_CMD, message);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(Error.ERR_INVALID_CMD, message, cause);
    }
}
