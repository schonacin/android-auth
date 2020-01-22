package com.blue_unicorn.android_auth_lib.fido;

public interface MakeCredentialResponse extends ResponseObject {

    void setFmt(String fmt);

    void setAuthData(byte[] authData);

    void setAttStmt(byte[] attStmt);

}
