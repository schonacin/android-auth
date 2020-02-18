package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions;

public abstract class BluetoothLowEnergyException extends Exception {

    private byte errorCode;

    public byte getErrorCode() {
        return errorCode;
    }

    BluetoothLowEnergyException(byte errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    BluetoothLowEnergyException(byte errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
