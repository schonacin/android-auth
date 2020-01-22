package com.blue_unicorn.android_auth_lib.fido;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class BaseGetInfoResponse implements GetInfoResponse {

    @SerializedName("1")
    private String[] versions;

    @SerializedName("3")
    private byte[] aaguid;

    @SerializedName("4")
    private Map<String, Boolean> options;

    @SerializedName("5")
    private int maxMsgSize;

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
