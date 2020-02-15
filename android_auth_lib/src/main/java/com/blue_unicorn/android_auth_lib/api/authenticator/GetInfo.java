package com.blue_unicorn.android_auth_lib.api.authenticator;

import com.blue_unicorn.android_auth_lib.fido.reponse.BaseGetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetInfoResponse;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

/**
 * Represents the authenticatorGetInfo method.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetInfo">specification</a>
 */
public class GetInfo {

    public Single<GetInfoResponse > operate(){
        return constructResponse();
    }

    private Single<Map<String, Boolean>> constructOptions() {
        return Single.defer(() -> {
            Map<String, Boolean> options = new HashMap<>();
            options.put("plat", Config.plat);
            options.put("rk", Config.rk);
            options.put("up", Config.up);
            options.put("uv", Config.uv);
            return Single.just(options);
        });
    }

    private Single<GetInfoResponse> constructResponse() {
        return Single.zip(Single.just(Config.versions), Single.just(Config.aaguid), constructOptions(), Single.just(Config.maxMsgSize), BaseGetInfoResponse::new);
    }

}
