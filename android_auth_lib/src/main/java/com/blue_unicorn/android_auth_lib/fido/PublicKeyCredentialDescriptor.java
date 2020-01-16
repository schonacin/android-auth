package com.blue_unicorn.android_auth_lib.fido;

import java.util.List;

/**
 * Reference: https://www.w3.org/TR/webauthn/#credential-dictionary
 *
 */
public interface PublicKeyCredentialDescriptor {

    String getType();

    byte[] getId();

    List<String> getTransports();
}
