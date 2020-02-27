package com.blue_unicorn.android_auth_lib.fido.webauthn;

/**
 * See <a href="https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialrpentity">reference</a>
 */
public interface PublicKeyCredentialRpEntity {

    String getId();

    String getName();

}