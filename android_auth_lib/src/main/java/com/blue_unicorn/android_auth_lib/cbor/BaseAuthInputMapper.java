package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.fido.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.GetInfoRequest;
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
                return buildGetAssertionRequest();
            case GET_INFO:
                return buildGetInfoRequest();
            default:
                return null;
        }

    }

    private MakeCredentialRequest buildMakeCredentialRequest() {

        return GsonHelper.customGson.fromJson(data, MakeCredentialRequest.class);

    }

    private GetAssertionRequest buildGetAssertionRequest() {

        return GsonHelper.customGson.fromJson(data, GetAssertionRequest.class);

    }

    private GetInfoRequest buildGetInfoRequest() {

        // getInfo does not have any parameters so no object mapping has to be done
        return new GetInfoRequest();

    }
}
