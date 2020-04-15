package com.blue_unicorn.android_auth_lib.ctap2.data.response;

import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.annotation.SerializedIndex;

import java.util.Map;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseGetInfoResponse implements GetInfoResponse {

    @SerializedIndex(1)
    private String[] versions;

    @SerializedIndex(3)
    private byte[] aaguid;

    @SerializedIndex(4)
    private Map<String, Boolean> options;

    @SerializedIndex(5)
    private Integer maxMsgSize;

    public BaseGetInfoResponse(String[] versions, byte[] aaguid) {
        this(versions, aaguid, null, null);
    }

    public BaseGetInfoResponse(String[] versions, byte[] aaguid, Map<String, Boolean> options, Integer maxMsgSize) {
        this.versions = versions;
        this.aaguid = aaguid;
        this.options = options;
        this.maxMsgSize = maxMsgSize;
    }

    public void setVersions(String[] versions) {
        this.versions = versions;
    }

    public void setAaguid(byte[] aaguid) {
        this.aaguid = aaguid;
    }

    public void setOptions(Map<String, Boolean> options) {
        this.options = options;
    }

    public void setMaxMsgSize(int maxMsgSize) {
        this.maxMsgSize = maxMsgSize;
    }

}
