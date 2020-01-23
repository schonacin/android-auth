package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants.Error;

public class AuthenticatorBusyException extends BluetoothLowEnergyException {

    public AuthenticatorBusyException(String message) {
        super(Error.ERR_BUSY, message);
    }

    public AuthenticatorBusyException(String message, Throwable cause) {
        super(Error.ERR_BUSY, message, cause);
    }
}
