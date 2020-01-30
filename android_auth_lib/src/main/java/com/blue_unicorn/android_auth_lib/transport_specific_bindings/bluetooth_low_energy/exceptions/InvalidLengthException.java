package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants.Error;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.BluetoothLowEnergyException;

public class InvalidLengthException extends BluetoothLowEnergyException {

    public InvalidLengthException(String message) {
        super(Error.ERR_INVALID_LEN, message);
    }

    public InvalidLengthException(String message, Throwable cause) {
        super(Error.ERR_INVALID_LEN, message, cause);
    }
}
