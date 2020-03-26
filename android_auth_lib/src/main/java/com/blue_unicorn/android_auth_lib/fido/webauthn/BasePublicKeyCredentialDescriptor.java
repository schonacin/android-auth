package com.blue_unicorn.android_auth_lib.fido.webauthn;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BasePublicKeyCredentialDescriptor implements PublicKeyCredentialDescriptor {

    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private byte[] id;

    @SerializedName("transports")
    private List<String> transports;

    public BasePublicKeyCredentialDescriptor(byte[] id) {
        this("public-key", id);
    }

    public BasePublicKeyCredentialDescriptor(String type, byte[] id) {
        this(type, id, null);
    }

    public BasePublicKeyCredentialDescriptor(String type, byte[] id, List<String> transports) {
        this.type = type;
        this.id = id;
        this.transports = transports;
    }

    public String getType() {
        return type;
    }

    public byte[] getId() {
        return id;
    }

    public List<String> getTransports() {
        return transports;
    }

}
