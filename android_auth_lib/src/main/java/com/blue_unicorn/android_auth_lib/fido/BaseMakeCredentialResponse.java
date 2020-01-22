package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

public class BaseMakeCredentialResponse implements MakeCredentialResponse {

    @SerializedName("1")
    private String fmt;

    @SerializedName("2")
    private byte[] authData;

    @SerializedName("3")
    private byte[] attStmt;

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public void setAuthData(byte[] authData) {
        this.authData = authData;
    }

    public void setAttStmt(byte[] attStmt) {
        this.attStmt = attStmt;
    }
}
