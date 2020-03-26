package com.blue_unicorn.android_auth_lib.ctap2.data.webauthn;

import com.google.gson.annotations.SerializedName;

public class BasePublicKeyCredentialParameter implements PublicKeyCredentialParameter {

    @SerializedName("type")
    private String type;

    @SerializedName("alg")
    private int alg;

    public String getType() {
        return type;
    }

    public int getAlg() {
        return alg;
    }

}
