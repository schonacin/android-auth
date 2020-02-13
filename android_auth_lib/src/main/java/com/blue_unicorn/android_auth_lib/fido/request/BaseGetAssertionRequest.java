package com.blue_unicorn.android_auth_lib.fido.request;

import androidx.annotation.Nullable;

import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.fido.webauthn.BasePublicKeyCredentialDescriptor;
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

    private boolean approved;

    // TODO extract this to interface?
    @Nullable
    private PublicKeyCredentialSource selectedCredential;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Nullable
    public PublicKeyCredentialSource getSelectedCredential() {
        return selectedCredential;
    }

    public void setSelectedCredential(@Nullable PublicKeyCredentialSource selectedCredential) {
        this.selectedCredential = selectedCredential;
    }

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

    public boolean isValid() {
        return (rpId != null && clientDataHash != null);
    }
}
