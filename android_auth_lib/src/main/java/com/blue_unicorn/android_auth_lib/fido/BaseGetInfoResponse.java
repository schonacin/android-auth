package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseGetInfoResponse implements GetInfoResponse {

    @SerializedName("1")
    private String[] versions;

    @SerializedName("3")
    private byte[] aaguid;

    @SerializedName("4")
    private Map<String, Boolean> options;

    @SerializedName("5")
    private Integer maxMsgSize;

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
