package com.blue_unicorn.android_auth_lib.fido;

import com.blue_unicorn.android_auth_lib.cbor.CborSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * These serialized names correlate to the specification.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">reference</a>
 */

public class BaseGetAssertionResponse implements GetAssertionResponse {

    private PublicKeyCredentialDescriptor credential;

    private byte[] authData;

    private byte[] signature;

    private PublicKeyCredentialUserEntity publicKeyCredentialUserEntity;

    private Integer numberOfCredentials;

    public void setCredential(PublicKeyCredentialDescriptor credential) {
        this.credential = credential;
    }

    public void setAuthData(byte[] authData) {
        this.authData = authData;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public void setPublicKeyCredentialUserEntity(PublicKeyCredentialUserEntity publicKeyCredentialUserEntity) {
        this.publicKeyCredentialUserEntity = publicKeyCredentialUserEntity;
    }

    public void setNumberOfCredentials(int numberOfCredentials) {
        this.numberOfCredentials = numberOfCredentials;
    }

    public byte[] serializeToCbor() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, this.credential);
        map.put(2, this.authData);
        map.put(3, this.signature);
        map.put(4, this.publicKeyCredentialUserEntity);
        map.put(5, this.numberOfCredentials);
        return CborSerializer.serialize(map);
    }

}
