package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.FidoObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.reponse.ResponseObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions.AuthLibException;

import io.reactivex.rxjava3.core.Single;

public class BaseAPIHandler implements APIHandler {

    private AuthenticatorAPI api;

    public BaseAPIHandler(Context context) {
        this.api = new BaseAuthenticatorAPI(context, true);
    }

    public Single<FidoObject> callAPI(RequestObject request) {
        return Single.defer(() -> {
            if (request instanceof MakeCredentialRequest) {
                return api.makeCredential((MakeCredentialRequest) request);
            } else if (request instanceof GetAssertionRequest) {
                return api.getAssertion((GetAssertionRequest) request);
            } else if (request instanceof GetInfoRequest) {
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
