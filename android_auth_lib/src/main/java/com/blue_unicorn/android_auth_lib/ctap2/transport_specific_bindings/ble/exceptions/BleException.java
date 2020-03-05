package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions;

/*
 * Subclasses represent one of the errors described in <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-constants"> each.
 */
public abstract class BleException extends Exception {

    private final byte errorCode;

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
