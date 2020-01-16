package com.blue_unicorn.android_auth_lib.fido;

/**
 * Reference: https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialuserentity
 *
 */
public interface PublicKeyCredentialUserEntity {

    byte[] getId();

    String getName();

    String getDisplayName();
}
