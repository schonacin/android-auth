package com.blue_unicorn.android_auth_lib.ctap2.data.webauthn;

/**
 * See <a href="https://www.w3.org/TR/webauthn/#attestation-statement">reference</a>
 */
public interface AttestationStatement {

    void setAlg(int alg);

    void setSig(byte[] sig);

    void setX5c(byte[][] x5c);

}
