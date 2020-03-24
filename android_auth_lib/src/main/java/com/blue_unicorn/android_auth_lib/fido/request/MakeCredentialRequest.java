package com.blue_unicorn.android_auth_lib.fido.request;

import com.blue_unicorn.android_auth_lib.fido.request.properties.Approvable;
import com.blue_unicorn.android_auth_lib.fido.request.properties.Excludable;
import com.blue_unicorn.android_auth_lib.fido.request.properties.Validatable;
import com.blue_unicorn.android_auth_lib.fido.webauthn.BasePublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.fido.webauthn.BasePublicKeyCredentialParameter;
import com.blue_unicorn.android_auth_lib.fido.webauthn.BasePublicKeyCredentialRpEntity;
import com.blue_unicorn.android_auth_lib.fido.webauthn.BasePublicKeyCredentialUserEntity;

import java.util.List;
import java.util.Map;

/**
 * Represents the parameters for the authenticatorMakeCredential method.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorMakeCredential">specification</a>
 */
public interface MakeCredentialRequest extends RequestObject, Excludable, Approvable, Validatable {

    byte[] getClientDataHash();

    BasePublicKeyCredentialRpEntity getRp();

    BasePublicKeyCredentialUserEntity getUser();

    BasePublicKeyCredentialParameter[] getPubKeyCredParams();

    List<BasePublicKeyCredentialDescriptor> getExcludeList();

    Map<String, Boolean> getOptions();

}
