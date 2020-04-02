package com.blue_unicorn.android_auth_lib.ctap2.data.webauthn;

import com.google.gson.annotations.SerializedName;

public class BasePublicKeyCredentialRpEntity implements PublicKeyCredentialRpEntity {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return (id != null);
    }
}
