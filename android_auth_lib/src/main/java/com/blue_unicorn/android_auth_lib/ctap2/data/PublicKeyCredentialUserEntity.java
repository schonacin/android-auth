package com.blue_unicorn.android_auth_lib.ctap2.data;

/**
 * See <a href="https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialuserentity">reference</a>
 */
public interface PublicKeyCredentialUserEntity {

    byte[] getId();

    String getName();

    String getDisplayName();

}
