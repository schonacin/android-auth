package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.FidoObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

public class BaseAPIHandler implements APIHandler {

    private AuthenticatorAPI api;

    public BaseAPIHandler(Context context) {
        this.api = new BaseAuthenticatorAPI(context, true);
    }

    public Single<FidoObject> callAPI(RequestObject request) {
        return Single.defer(() -> {
            if (request instanceof MakeCredentialRequest) {
                Timber.d("Call API for makeCrendential");
                return api.makeCredential((MakeCredentialRequest) request);
            } else if (request instanceof GetAssertionRequest) {
                Timber.d("Call API for getAssertion");
                return api.getAssertion((GetAssertionRequest) request);
            } else if (request instanceof GetInfoRequest) {
                Timber.d("Call API for getInfo");
                // start sending keep alives with processing status
                return api.getInfo((GetInfoRequest) request);
            } else {
                return Single.error(new AuthLibException("invalid request object for calling api"));
            }
        });
    }

    public Single<ResponseObject> updateAPI(RequestObject request) {
        return Single.defer(() -> {
            if (request instanceof MakeCredentialRequest) {
                return api.makeInternalCredential((MakeCredentialRequest) request);
            } else if (request instanceof GetAssertionRequest) {
                return api.getInternalAssertion((GetAssertionRequest) request);
            } else {
                return Single.error(new AuthLibException("invalid request object for updating api"));
            }
        });
    }

}
