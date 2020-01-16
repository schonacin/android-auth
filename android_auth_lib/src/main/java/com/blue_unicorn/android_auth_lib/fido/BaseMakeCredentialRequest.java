package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * These serialized names correlate to the the table specified in:
 * https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands
 *
 */
public class BaseMakeCredentialRequest implements MakeCredentialRequest {

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
