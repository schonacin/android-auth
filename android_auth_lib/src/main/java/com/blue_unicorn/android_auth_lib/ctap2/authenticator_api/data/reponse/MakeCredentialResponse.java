package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.reponse;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.AttestationStatement;

/**
 * Represents the data the authenticatorMakeCredential method returns.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorMakeCredential">specification</a>
 */
public interface MakeCredentialResponse extends ResponseObject {

    void setFmt(String fmt);

    void setAuthData(byte[] authData);

    void setAttStmt(AttestationStatement attStmt);

}
