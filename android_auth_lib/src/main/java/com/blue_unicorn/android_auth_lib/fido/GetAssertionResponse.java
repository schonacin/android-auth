package com.blue_unicorn.android_auth_lib.fido;

public interface GetAssertionResponse extends ResponseObject {

    void setCredential(PublicKeyCredentialDescriptor credential);

    void setAuthData(byte[] authData);

    void setSignature(byte[] signature);

    void setPublicKeyCredentialUserEntity(PublicKeyCredentialUserEntity publicKeyCredentialUserEntity);

    void setNumberOfCredentials(int numberOfCredentials);

}