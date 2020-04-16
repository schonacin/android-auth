package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.properties.Approvable;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.properties.Validatable;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Represents the parameters for the authenticatorGetAssertion method.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetAssertion">specification</a>
 */
public interface GetAssertionRequest extends RequestObject, Approvable, Validatable {

    String getRpId();

    byte[] getClientDataHash();

    List<BasePublicKeyCredentialDescriptor> getAllowList();

    Map<String, Boolean> getOptions();

    List<PublicKeyCredentialSource> getSelectedCredentials();

    void setSelectedCredentials(List<PublicKeyCredentialSource> selectedCredentials);

}
