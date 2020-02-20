package com.blue_unicorn.android_auth_lib.fido.webauthn;

/**
 * See <a href="https://www.w3.org/TR/webauthn/#credential-params">reference</a>
 */
public interface PublicKeyCredentialParameter {

    String getType();

    int getAlg();

}
