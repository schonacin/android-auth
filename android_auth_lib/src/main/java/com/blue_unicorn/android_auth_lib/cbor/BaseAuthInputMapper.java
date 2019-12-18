package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.fido.FidoObject;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;
import com.blue_unicorn.android_auth_lib.gson.GsonHelper;

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

    public RequestObject mapRespectiveCommand() {

        switch (this.cmd) {
            case MAKE_CREDENTIAL:
                return buildMakeCredentialRequest();
            case GET_ASSERTION:
                return null;
            case GET_INFO:
                return null;
            default:
                return null;
        }

    }

    private MakeCredentialRequest buildMakeCredentialRequest() {

        return GsonHelper.customGson.fromJson(data, MakeCredentialRequest.class);

    }

    private FidoObject buildGetAssertionRequest() {
        return new FidoObject() {};
    }

    private FidoObject buildGetInfoRequest() {
        return new FidoObject() {};
    }
}
