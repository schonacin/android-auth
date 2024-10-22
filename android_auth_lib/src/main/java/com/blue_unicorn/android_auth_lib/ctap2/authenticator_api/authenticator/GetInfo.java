package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.BaseGetInfoResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.GetInfoResponse;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

/**
 * Represents the authenticatorGetInfo method.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticatorGetInfo">specification</a>
 */
public class GetInfo {

    public Single<GetInfoResponse> operate() {
        return constructResponse();
    }

    private Single<Map<String, Boolean>> constructOptions() {
        return Single.fromCallable(() -> {
            Map<String, Boolean> options = new HashMap<>();
            options.put("plat", Config.PLAT);
            options.put("rk", Config.RK);
            options.put("up", Config.UP);
            options.put("uv", Config.UV);
            return options;
        });
    }

    private Single<GetInfoResponse> constructResponse() {
        return Single.zip(Single.just(Config.VERSIONS), Single.just(Config.AAGUID), constructOptions(), Single.just(Config.MAX_MSG_SIZE), BaseGetInfoResponse::new);
    }

}
