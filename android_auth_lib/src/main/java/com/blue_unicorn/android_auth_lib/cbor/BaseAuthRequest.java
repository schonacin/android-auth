package com.blue_unicorn.android_auth_lib.cbor;

class BaseAuthRequest implements AuthRequest {

    private byte cmd;
    private byte[] data;

    BaseAuthRequest(byte cmd, byte[] data) {
        this.cmd = cmd;
        this.data = data;
    }

    public byte getCmd() {
        return cmd;
    }

    public byte[] getData() {
        return data;
    }
}
