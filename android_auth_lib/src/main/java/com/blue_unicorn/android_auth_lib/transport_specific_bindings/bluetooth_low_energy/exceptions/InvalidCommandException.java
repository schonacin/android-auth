package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants.Error;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.BluetoothLowEnergyException;

public class InvalidCommandException extends BluetoothLowEnergyException {

    public InvalidCommandException(String message) {
        super(Error.ERR_INVALID_CMD, message);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(Error.ERR_INVALID_CMD, message, cause);
    }
}
