package com.blue_unicorn.android_auth_lib.fido;

import com.blue_unicorn.android_auth_lib.cbor.CborSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */
public class BaseMakeCredentialResponse implements MakeCredentialResponse {

    private String fmt;

    private byte[] authData;

    private AttestationStatement attStmt;

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public void setAuthData(byte[] authData) {
        this.authData = authData;
    }

    public void setAttStmt(AttestationStatement attStmt) {
        this.attStmt = attStmt;
    }

    public byte[] serializeToCbor() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, this.fmt);
        map.put(2, this.authData);
        map.put(3, this.attStmt);
        return CborSerializer.serialize(map);
    }
}
