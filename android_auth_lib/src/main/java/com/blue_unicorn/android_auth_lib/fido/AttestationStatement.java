package com.blue_unicorn.android_auth_lib.fido;

public interface AttestationStatement {

    void setAlg(int alg);

    void setSig(byte[] sig);

    void setX5c(byte[][] x5c);

}
