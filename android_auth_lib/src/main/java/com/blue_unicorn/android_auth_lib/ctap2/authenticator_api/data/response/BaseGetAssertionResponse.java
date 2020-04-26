package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialUserEntity;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.annotations.SerializedIndex;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseGetAssertionResponse implements GetAssertionResponse {

    @SerializedIndex(1)
    private PublicKeyCredentialDescriptor credential;

    @SerializedIndex(2)
    private byte[] authData;

    @SerializedIndex(3)
    private byte[] signature;

    @SerializedIndex(4)
    private PublicKeyCredentialUserEntity user;

    @SerializedIndex(5)
    private Integer numberOfCredentials;

    public BaseGetAssertionResponse(PublicKeyCredentialDescriptor credential, byte[] authData, byte[] signature) {
        this(credential, authData, signature, null);
    }

    public BaseGetAssertionResponse(PublicKeyCredentialDescriptor credential, byte[] authData, byte[] signature, PublicKeyCredentialUserEntity user) {
        this(credential, authData, signature, user, null);
    }

    public BaseGetAssertionResponse(PublicKeyCredentialDescriptor credential, byte[] authData, byte[] signature, PublicKeyCredentialUserEntity user, Integer numberOfCredentials) {
        this.credential = credential;
        this.authData = authData;
        this.signature = signature;
        this.user = user;
        this.numberOfCredentials = numberOfCredentials;
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

    public void setPublicKeyCredentialUserEntity(PublicKeyCredentialUserEntity user) {
        this.user = user;
    }

    public void setNumberOfCredentials(int numberOfCredentials) {
        this.numberOfCredentials = numberOfCredentials;
    }

}
