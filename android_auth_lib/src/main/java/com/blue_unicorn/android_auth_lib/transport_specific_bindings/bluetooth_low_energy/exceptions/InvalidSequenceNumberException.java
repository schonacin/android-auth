package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants.Error;

public class InvalidSequenceNumberException extends BluetoothLowEnergyException {

    public InvalidSequenceNumberException(String message) {
        super(Error.ERR_BUSY, message);
    }

    public InvalidSequenceNumberException(String message, Throwable cause) {
        super(Error.ERR_BUSY, message, cause);
    }
}
