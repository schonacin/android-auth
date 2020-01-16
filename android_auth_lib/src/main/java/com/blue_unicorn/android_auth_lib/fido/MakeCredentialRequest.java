package com.blue_unicorn.android_auth_lib.fido;

import java.util.List;
import java.util.Map;

/**
 * Represents the parameters for the authenticatorMakeCredential method as described in:
 * https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorMakeCredential
 *
 */

public interface MakeCredentialRequest extends RequestObject {

    byte[] getClientDataHash();

    BasePublicKeyCredentialRpEntity getRp();

    BasePublicKeyCredentialUserEntity getUser();

    Map[] getPubKeyCredParams();

    List<BasePublicKeyCredentialDescriptor> getExcludeList();

    Map<String, Boolean> getOptions();

}
