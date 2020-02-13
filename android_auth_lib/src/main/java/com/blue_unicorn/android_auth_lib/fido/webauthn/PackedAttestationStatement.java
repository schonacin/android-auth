package com.blue_unicorn.android_auth_lib.fido.webauthn;

import com.google.gson.annotations.SerializedName;

/**
 * See <a href="https://www.w3.org/TR/webauthn/#packed-attestation">reference</a>
 */
public class PackedAttestationStatement implements AttestationStatement {

    @SerializedName("alg")
    private int alg;

    @SerializedName("sig")
    private byte[] sig;

    @SerializedName("x5c")
    private byte[][] x5c;

    public PackedAttestationStatement(byte[] sig) {
        // TODO outsource algorithm identifier to Config or somewhere else
        this.alg= -7;
        this.sig = sig;
    }

    public void setAlg(int alg) {
        this.alg = alg;
    }

    public void setSig(byte[] sig) {
        this.sig = sig;
    }

    public void setX5c(byte[][] x5c) {
        this.x5c = x5c;
    }

}
