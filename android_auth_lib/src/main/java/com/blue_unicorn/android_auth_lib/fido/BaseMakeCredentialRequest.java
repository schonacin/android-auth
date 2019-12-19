package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class BaseMakeCredentialRequest extends RequestObject implements MakeCredentialRequest {

    @SerializedName("1")
    private byte[] clientDataHash;

    @SerializedName("2")
    private BasePublicKeyCredentialRpEntity rp;

    @SerializedName("3")
    private BasePublicKeyCredentialUserEntity user;

    @SerializedName("4")
    private Map[] pubKeyCredParams;

    @SerializedName("5")
    private List<BasePublicKeyCredentialDescriptor> excludeList;

    @SerializedName("6")
    private Map<String, Boolean> options;

    public byte[] getClientDataHash() {
        return clientDataHash;
    }

    public BasePublicKeyCredentialRpEntity getRp() {
        return rp;
    }

    public BasePublicKeyCredentialUserEntity getUser() {
        return user;
    }

    public Map[] getPubKeyCredParams() {
        return pubKeyCredParams;
    }

    public List<BasePublicKeyCredentialDescriptor> getExcludeList() {
        return excludeList;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }
}
