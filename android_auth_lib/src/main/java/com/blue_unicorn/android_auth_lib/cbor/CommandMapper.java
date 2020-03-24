package com.blue_unicorn.android_auth_lib.cbor;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.exception.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.fido.request.BaseGetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.request.BaseGetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.request.BaseMakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.request.RequestObject;
import com.blue_unicorn.android_auth_lib.gson.GsonHelper;
import com.google.gson.Gson;

import io.reactivex.rxjava3.core.Single;

final class CommandMapper {

    private final static byte MAKE_CREDENTIAL = 0x01;
    private final static byte GET_ASSERTION = 0x02;
    private final static byte GET_INFO = 0x04;

    /*
     * Maps decoded commands to RequestObjects by respective methods.
     * Currently supported methods are:
     *   getInfo
     *   makeCredential
     *   getAssertion
     *
     * */

    private CommandMapper() {
    }

    @NonNull
    static Single<RequestObject> mapRespectiveMethod(Command command) {
        return Single.defer(() -> {
            switch (command.getValue()) {
                case MAKE_CREDENTIAL:
                    return Single.fromCallable(() -> buildMakeCredentialRequest(command.getParameters()));
                case GET_ASSERTION:
                    return Single.fromCallable(() -> buildGetAssertionRequest(command.getParameters()));
                case GET_INFO:
                    return Single.fromCallable(CommandMapper::buildGetInfoRequest);
                default:
                    return Single.error(new InvalidCommandException());
            }
        });
    }

    @NonNull
    private static MakeCredentialRequest buildMakeCredentialRequest(String data) {
        Gson byteArraytoJsonGson = GsonHelper.getInstance().byteArraytoJsonGson;
        return byteArraytoJsonGson.fromJson(data, BaseMakeCredentialRequest.class);
    }

    @NonNull
    private static GetAssertionRequest buildGetAssertionRequest(String data) {
        Gson byteArraytoJsonGson = GsonHelper.getInstance().byteArraytoJsonGson;
        return byteArraytoJsonGson.fromJson(data, BaseGetAssertionRequest.class);
    }

    @NonNull
    private static GetInfoRequest buildGetInfoRequest() {
        // getInfo does not have any parameters so no object mapping has to be done
        return new BaseGetInfoRequest();
    }

}
