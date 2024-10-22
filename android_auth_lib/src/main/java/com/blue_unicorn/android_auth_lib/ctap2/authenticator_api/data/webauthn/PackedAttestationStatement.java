package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.Config;
import com.google.gson.annotations.SerializedName;
import com.nexenio.rxandroidbleserver.service.value.ValueUtil;

import java.util.Arrays;

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
        this(Config.COSE_ALGORITHM_IDENTIFIER, sig);
    }

    public PackedAttestationStatement(int alg, byte[] sig) {
        this(alg, sig, null);
    }

    public PackedAttestationStatement(int alg, byte[] sig, byte[][] x5c) {
        this.alg = alg;
        this.sig = sig;
        this.x5c = x5c;
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

    @Override
    public String toString() {
        return "PackedAttestationStatement{" +
                "alg=" + alg +
                ", sig=" + ValueUtil.bytesToHex(sig) +
                ", x5c=" + Arrays.toString(x5c) +
                '}';
    }
}
