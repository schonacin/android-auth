package com.blue_unicorn.android_auth_lib.ctap2.data.webauthn;

import com.google.gson.annotations.SerializedName;

public class BasePublicKeyCredentialUserEntity implements PublicKeyCredentialUserEntity {

    @SerializedName("id")
    private byte[] id;

    @SerializedName("name")
    private String name;

    @SerializedName("displayName")
    private String displayName;

    public byte[] getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isValid() {
        return (id != null && name != null);
    }

}
