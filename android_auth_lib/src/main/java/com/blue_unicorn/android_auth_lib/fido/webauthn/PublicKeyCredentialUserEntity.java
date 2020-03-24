package com.blue_unicorn.android_auth_lib.fido.webauthn;

/**
 * See <a href="https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialuserentity">reference</a>
 */
public interface PublicKeyCredentialUserEntity {

    byte[] getId();

    String getName();

    String getDisplayName();

}
