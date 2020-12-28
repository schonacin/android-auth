package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response;

import java.util.Map;

/**
 * Represents the data the authenticatorGetInfo method returns.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetInfo">specification</a>
 */
public interface GetInfoResponse extends ResponseObject {

    void setVersions(String[] versions);

    void setExtensions(String[] extensions);

    void setAaguid(byte[] aaguid);

    void setOptions(Map<String, Boolean> options);

    void setMaxMsgSize(int maxMsgSize);

}
