package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

public class BasePublicKeyCredentialUserEntity implements PublicKeyCredentialUserEntity {

    @SerializedName("id")
    private byte[] id;

    @SerializedName("name")
    private String name;

    @SerializedName("displayName")
    private String displayName;

    public BasePublicKeyCredentialUserEntity(byte[] id) {
        this.id = id;
    }

    public BasePublicKeyCredentialUserEntity(byte[] id, String name) {
        this.id = id;
        this.name = name;
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

}
