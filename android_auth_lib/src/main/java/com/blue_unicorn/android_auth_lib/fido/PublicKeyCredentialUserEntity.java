package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

public class PublicKeyCredentialUserEntity {

    @SerializedName("id")
    public byte[] id;

    @SerializedName("name")
    public String name;

    @SerializedName("displayName")
    public String displayName;
}
