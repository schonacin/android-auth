package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Error;

public class InvalidParameterException extends BluetoothLowEnergyException {

    public InvalidParameterException(String message) {
        super(Error.ERR_INVALID_PAR, message);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(Error.ERR_INVALID_PAR, message, cause);
    }
}
