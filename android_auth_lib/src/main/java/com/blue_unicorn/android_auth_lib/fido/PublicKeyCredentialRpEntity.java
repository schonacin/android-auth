package com.blue_unicorn.android_auth_lib.fido;

/**
 * Reference: https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialrpentity
 *
 */
public interface PublicKeyCredentialRpEntity {

    String getId();

    String getName();

}
