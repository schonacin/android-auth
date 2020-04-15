package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn;

import java.util.List;

/**
 * See <a href="https://www.w3.org/TR/webauthn/#credential-dictionary">reference</a>
 */
public interface PublicKeyCredentialDescriptor {

    String getType();

    byte[] getId();

    List<String> getTransports();

}
