package com.blue_unicorn.android_auth_lib.ctap2.data.reponse;

import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.PublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.PublicKeyCredentialUserEntity;

/**
 * Represents the data the authenticatorGetAssertion method returns.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetAssertion">specification</a>
 */
public interface GetAssertionResponse extends ResponseObject {

    void setCredential(PublicKeyCredentialDescriptor credential);

    void setAuthData(byte[] authData);

    void setSignature(byte[] signature);

    void setPublicKeyCredentialUserEntity(PublicKeyCredentialUserEntity publicKeyCredentialUserEntity);

    void setNumberOfCredentials(int numberOfCredentials);

}