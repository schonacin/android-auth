package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.exception.InvalidCmdException;
import com.blue_unicorn.android_auth_lib.fido.BaseGetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseGetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseMakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;
import com.blue_unicorn.android_auth_lib.gson.GsonHelper;

import io.reactivex.rxjava3.core.Observable;

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

    public Observable<RequestObject> mapRespectiveCommand() {

        /*
        * currently supported methods are:
        *
        *   getInfo
        *   makeCredential
        *   getAssertion
        *
        * */

        switch (this.cmd) {
            case MAKE_CREDENTIAL:
                return Observable.fromCallable(this::buildMakeCredentialRequest);
            case GET_ASSERTION:
                return Observable.fromCallable(this::buildGetAssertionRequest);
            case GET_INFO:
                return Observable.fromCallable(this::buildGetInfoRequest);
            default:
                return Observable.error(new InvalidCmdException());
        }

    }

    private MakeCredentialRequest buildMakeCredentialRequest() {

        return GsonHelper.customGson.fromJson(data, BaseMakeCredentialRequest.class);

    }

    private GetAssertionRequest buildGetAssertionRequest() {

        return GsonHelper.customGson.fromJson(data, BaseGetAssertionRequest.class);

    }

    private GetInfoRequest buildGetInfoRequest() {

        // getInfo does not have any parameters so no object mapping has to be done
        return new BaseGetInfoRequest();

    }
}
