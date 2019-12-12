package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.fido.FidoObject;

public class BaseAuthInputMapper implements AuthInputMapper {

    private final static byte MAKE_CREDENTIAL = 0x01;
    private final static byte GET_ASSERTION = 0x02;
    private final static byte GET_INFO = 0x04;

    private byte cmd;
    private String data;

    BaseAuthInputMapper(byte cmd, String data) {
        this.cmd = cmd;
        this.data = data;
    }

    public FidoObject mapRespectiveCommand() {

        switch (this.cmd) {
            case MAKE_CREDENTIAL:
                return buildMakeCredentialRequest();
            case GET_ASSERTION:
                return buildGetAssertionRequest();
            case GET_INFO:
                return buildGetInfoRequest();
            default:
                return null;
        }

    }

    private FidoObject buildMakeCredentialRequest() {
        return new FidoObject() {};
    }

    private FidoObject buildGetAssertionRequest() {
        return new FidoObject() {};
    }

    private FidoObject buildGetInfoRequest() {
        return new FidoObject() {};
    }
}
