package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class MakeCredentialRequest extends RequestObject {

    @SerializedName("1")
    byte[] clientDataHash;

    @SerializedName("2")
    PublicKeyCredentialRpEntity rp;

    @SerializedName("3")
    PublicKeyCredentialUserEntity user;

    @SerializedName("4")
    Map[] pubKeyCredParams;

    @SerializedName("5")
    List<PublicKeyCredentialDescriptor> excludeList;

    @SerializedName("6")
    Map<String, Boolean> options;

}
