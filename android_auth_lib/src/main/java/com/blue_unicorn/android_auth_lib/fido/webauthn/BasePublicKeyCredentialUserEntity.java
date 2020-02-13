package com.blue_unicorn.android_auth_lib.fido.webauthn;

import com.google.gson.annotations.SerializedName;

public class BasePublicKeyCredentialUserEntity implements PublicKeyCredentialUserEntity {

    @SerializedName("id")
    private byte[] id;

    @SerializedName("name")
    private String name;

    @SerializedName("displayName")
    private String displayName;

    public BasePublicKeyCredentialUserEntity(byte[] id) {
        this(id, null, null);
    }

    public BasePublicKeyCredentialUserEntity(byte[] id, String name) {
        this(id, name, null);
    }

    public BasePublicKeyCredentialUserEntity(byte[] id, String name, String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
    }

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
