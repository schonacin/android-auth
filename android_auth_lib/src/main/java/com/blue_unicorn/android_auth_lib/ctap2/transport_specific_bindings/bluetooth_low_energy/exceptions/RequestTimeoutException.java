package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.constants.Error;

public class RequestTimeoutException extends BluetoothLowEnergyException {

    public RequestTimeoutException(String message) {
        super(Error.ERR_REQ_TIMEOUT, message);
    }

    public RequestTimeoutException(String message, Throwable cause) {
        super(Error.ERR_REQ_TIMEOUT, message, cause);
    }
}
