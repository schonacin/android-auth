package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PublicKeyCredentialDescriptor {

    @SerializedName("type")
    public String type;

    @SerializedName("id")
    public byte[] id;

    @SerializedName("transports")
    public List<String> transports;
}
