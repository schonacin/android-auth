package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions;

public abstract class BluetoothLowEnergyException extends Exception {

    private byte errorCode;

    public byte getErrorCode() {
        return errorCode;
    }

    public BluetoothLowEnergyException(byte errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BluetoothLowEnergyException(byte errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
