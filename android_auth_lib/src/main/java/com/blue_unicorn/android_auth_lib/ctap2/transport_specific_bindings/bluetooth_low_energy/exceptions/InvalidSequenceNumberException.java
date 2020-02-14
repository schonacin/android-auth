package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.constants.Error;

public class InvalidSequenceNumberException extends BluetoothLowEnergyException {

    public InvalidSequenceNumberException(String message) {
        super(Error.ERR_INVALID_SEQ, message);
    }

    public InvalidSequenceNumberException(String message, Throwable cause) {
        super(Error.ERR_INVALID_SEQ, message, cause);
    }
}
