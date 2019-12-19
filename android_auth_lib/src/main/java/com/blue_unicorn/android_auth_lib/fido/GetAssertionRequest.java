package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class GetAssertionRequest extends RequestObject {

    @SerializedName("1")
    private String rpId;

    @SerializedName("2")
    private byte[] clientDataHash;

    @SerializedName("3")
    private List<PublicKeyCredentialDescriptor> allowList;

    @SerializedName("5")
    private Map<String, Boolean> options;

    public String getRpId() {
        return rpId;
    }

    public byte[] getClientDataHash() {
        return clientDataHash;
    }

    public List<PublicKeyCredentialDescriptor> getAllowList() {
        return allowList;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }
}
