package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseGetAssertionRequest implements GetAssertionRequest {

    @SerializedName("1")
    private String rpId;

    @SerializedName("2")
    private byte[] clientDataHash;

    @SerializedName("3")
    private List<BasePublicKeyCredentialDescriptor> allowList;

    @SerializedName("5")
    private Map<String, Boolean> options;

    public String getRpId() {
        return rpId;
    }

    public byte[] getClientDataHash() {
        return clientDataHash;
    }

    public List<BasePublicKeyCredentialDescriptor> getAllowList() {
        return allowList;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }
}
