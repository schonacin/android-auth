package com.blue_unicorn.android_auth_lib.fido.request;

import androidx.annotation.Nullable;

import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.fido.user.Approvable;
import com.blue_unicorn.android_auth_lib.fido.webauthn.BasePublicKeyCredentialDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Represents the parameters for the authenticatorGetAssertion method.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetAssertion">specification</a>
 */
public interface GetAssertionRequest extends RequestObject, Approvable {

    String getRpId();

    byte[] getClientDataHash();

    List<BasePublicKeyCredentialDescriptor> getAllowList();

    Map<String, Boolean> getOptions();

    @Nullable
    PublicKeyCredentialSource getSelectedCredential();

    void setSelectedCredential(@Nullable PublicKeyCredentialSource selectedCredential);

}
