package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

public class PackedAttestationStatement implements AttestationStatement {

    @SerializedName("alg")
    private int alg;

    @SerializedName("sig")
    private byte[] sig;

    @SerializedName("x5c")
    private byte[][] x5c;

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
