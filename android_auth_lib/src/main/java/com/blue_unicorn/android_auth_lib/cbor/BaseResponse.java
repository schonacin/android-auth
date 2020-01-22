package com.blue_unicorn.android_auth_lib.cbor;

public class BaseResponse implements Response {

    private byte status;

    private String data;

    public BaseResponse(byte status, String data) {
        this.status = status;
        this.data = data;
    }

    public byte getStatus() {
        return status;
    }

    public String getData() {
        return data;
    }

}
