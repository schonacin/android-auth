package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class MakeCredentialRequest extends RequestObject {

    @SerializedName("1")
    private byte[] clientDataHash;

    @SerializedName("2")
    private PublicKeyCredentialRpEntity rp;

    @SerializedName("3")
    private PublicKeyCredentialUserEntity user;

    @SerializedName("4")
    private Map[] pubKeyCredParams;

    @SerializedName("5")
    private List<PublicKeyCredentialDescriptor> excludeList;

    @SerializedName("6")
    private Map<String, Boolean> options;

    public byte[] getClientDataHash() {
        return clientDataHash;
    }

    public PublicKeyCredentialRpEntity getRp() {
        return rp;
    }

    public PublicKeyCredentialUserEntity getUser() {
        return user;
    }

    public Map[] getPubKeyCredParams() {
        return pubKeyCredParams;
    }

    public List<PublicKeyCredentialDescriptor> getExcludeList() {
        return excludeList;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }
}
