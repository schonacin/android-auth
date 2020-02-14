package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.constants.Error;

public class InvalidLengthException extends BluetoothLowEnergyException {

    public InvalidLengthException(String message) {
        super(Error.ERR_INVALID_LEN, message);
    }

    public InvalidLengthException(String message, Throwable cause) {
        super(Error.ERR_INVALID_LEN, message, cause);
    }
}
