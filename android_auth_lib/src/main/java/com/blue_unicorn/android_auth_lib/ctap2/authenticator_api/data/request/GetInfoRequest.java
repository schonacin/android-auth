package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.ExtensionSupport;

/**
 * This class is empty as the authenticatorGetInfo method has no parameters.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetInfo">specification</a>
 */
public interface GetInfoRequest extends RequestObject {

    ExtensionSupport getExtensionSupport();

    void setExtensionSupport(ExtensionSupport extensionSupport);

}
