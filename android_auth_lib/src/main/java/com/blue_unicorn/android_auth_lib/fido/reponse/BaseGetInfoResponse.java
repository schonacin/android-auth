package com.blue_unicorn.android_auth_lib.fido.reponse;

import com.blue_unicorn.android_auth_lib.cbor.CborSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseGetInfoResponse implements GetInfoResponse {

    private String[] versions;

    private byte[] aaguid;

    private Map<String, Boolean> options;

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

    public byte[] serializeToCbor() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, this.versions);
        map.put(3, this.aaguid);
        map.put(4, this.options);
        map.put(5, this.maxMsgSize);
        return CborSerializer.serialize(map);
    }

}
