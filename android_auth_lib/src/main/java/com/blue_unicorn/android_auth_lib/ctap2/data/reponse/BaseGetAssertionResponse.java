package com.blue_unicorn.android_auth_lib.ctap2.data.reponse;

import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.PublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.PublicKeyCredentialUserEntity;
import com.google.gson.annotations.SerializedName;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseGetAssertionResponse implements GetAssertionResponse {

    @SerializedName("1")
    private PublicKeyCredentialDescriptor credential;

    @SerializedName("2")
    private byte[] authData;

    @SerializedName("3")
    private byte[] signature;

    @SerializedName("4")
    private PublicKeyCredentialUserEntity publicKeyCredentialUserEntity;

    @SerializedName("5")
    private Integer numberOfCredentials;

    public BaseGetAssertionResponse(PublicKeyCredentialDescriptor credential, byte[] authData, byte[] signature) {
        this.credential = credential;
        this.authData = authData;
        this.signature = signature;
    }

    public void setCredential(PublicKeyCredentialDescriptor credential) {
        this.credential = credential;
    }

    public void setAuthData(byte[] authData) {
        this.authData = authData;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public void setPublicKeyCredentialUserEntity(PublicKeyCredentialUserEntity publicKeyCredentialUserEntity) {
        this.publicKeyCredentialUserEntity = publicKeyCredentialUserEntity;
    }

    public void setNumberOfCredentials(int numberOfCredentials) {
        this.numberOfCredentials = numberOfCredentials;
    }

}
