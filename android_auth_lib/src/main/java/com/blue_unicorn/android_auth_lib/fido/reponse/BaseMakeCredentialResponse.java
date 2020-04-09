package com.blue_unicorn.android_auth_lib.fido.reponse;

import com.blue_unicorn.android_auth_lib.cbor.SerializedIndex;
import com.blue_unicorn.android_auth_lib.fido.webauthn.AttestationStatement;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseMakeCredentialResponse implements MakeCredentialResponse {

    @SerializedIndex(1)
    private String fmt;

    @SerializedIndex(2)
    private byte[] authData;

    @SerializedIndex(3)
    private AttestationStatement attStmt;

    public BaseMakeCredentialResponse(byte[] authData, AttestationStatement attStmt) {
        this("packed", authData, attStmt);
    }

    public BaseMakeCredentialResponse(String fmt, byte[] authData, AttestationStatement attStmt) {
        this.fmt = fmt;
        this.authData = authData;
        this.attStmt = attStmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public void setAuthData(byte[] authData) {
        this.authData = authData;
    }

    public void setAttStmt(AttestationStatement attStmt) {
        this.attStmt = attStmt;
    }

}
