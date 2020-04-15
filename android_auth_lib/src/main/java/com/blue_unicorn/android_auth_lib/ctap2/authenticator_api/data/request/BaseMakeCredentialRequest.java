package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialParameter;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialRpEntity;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialUserEntity;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseMakeCredentialRequest implements MakeCredentialRequest {

    @SerializedName("1")
    private byte[] clientDataHash;

    @SerializedName("2")
    private BasePublicKeyCredentialRpEntity rp;

    @SerializedName("3")
    private BasePublicKeyCredentialUserEntity user;

    @SerializedName("4")
    private BasePublicKeyCredentialParameter[] pubKeyCredParams;

    @SerializedName("5")
    private List<BasePublicKeyCredentialDescriptor> excludeList;

    @SerializedName("6")
    private Map<String, Boolean> options;

    private boolean excluded;
    private boolean approved;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }

    public byte[] getClientDataHash() {
        return clientDataHash;
    }

    public BasePublicKeyCredentialRpEntity getRp() {
        return rp;
    }

    public BasePublicKeyCredentialUserEntity getUser() {
        return user;
    }

    public BasePublicKeyCredentialParameter[] getPubKeyCredParams() {
        return pubKeyCredParams;
    }

    public List<BasePublicKeyCredentialDescriptor> getExcludeList() {
        return excludeList;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }

    public boolean isValid() {
        return (clientDataHash != null && rp.isValid() && user.isValid());
    }
}
