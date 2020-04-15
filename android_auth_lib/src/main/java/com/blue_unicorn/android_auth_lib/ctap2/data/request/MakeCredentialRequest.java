package com.blue_unicorn.android_auth_lib.ctap2.data.request;

import com.blue_unicorn.android_auth_lib.ctap2.data.request.properties.Approvable;
import com.blue_unicorn.android_auth_lib.ctap2.data.request.properties.Excludable;
import com.blue_unicorn.android_auth_lib.ctap2.data.request.properties.Validatable;
import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.BasePublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.BasePublicKeyCredentialParameter;
import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.BasePublicKeyCredentialRpEntity;
import com.blue_unicorn.android_auth_lib.ctap2.data.webauthn.BasePublicKeyCredentialUserEntity;

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
