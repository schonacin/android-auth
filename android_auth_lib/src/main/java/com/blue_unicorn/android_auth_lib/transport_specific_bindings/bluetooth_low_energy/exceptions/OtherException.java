package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants.Error;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.BluetoothLowEnergyException;

public class OtherException extends BluetoothLowEnergyException {

    public OtherException(String message) {
        super(Error.ERR_OTHER, message);
    }

    public OtherException(String message, Throwable cause) {
        super(Error.ERR_OTHER, message, cause);
    }
}
