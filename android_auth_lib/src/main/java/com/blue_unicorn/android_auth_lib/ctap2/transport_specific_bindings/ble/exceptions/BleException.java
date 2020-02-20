package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions;

public abstract class BleException extends Exception {

    private byte errorCode;

    public byte getErrorCode() {
        return errorCode;
    }

    BleException(byte errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    BleException(byte errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
