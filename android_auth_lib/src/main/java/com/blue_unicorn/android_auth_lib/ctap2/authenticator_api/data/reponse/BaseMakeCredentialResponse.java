package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.reponse;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.AttestationStatement;
import com.google.gson.annotations.SerializedName;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseMakeCredentialResponse implements MakeCredentialResponse {

    @SerializedName("1")
    private String fmt;

    @SerializedName("2")
    private byte[] authData;

    @SerializedName("3")
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
