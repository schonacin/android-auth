package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

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
    private int numberOfCredentials;

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
