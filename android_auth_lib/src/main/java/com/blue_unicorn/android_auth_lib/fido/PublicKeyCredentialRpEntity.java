package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

public class PublicKeyCredentialRpEntity {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;
}
