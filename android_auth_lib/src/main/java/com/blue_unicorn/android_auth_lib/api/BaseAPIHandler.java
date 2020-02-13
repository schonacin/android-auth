package com.blue_unicorn.android_auth_lib.api;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.AuthLibException;
import com.blue_unicorn.android_auth_lib.api.authenticator.AuthenticatorAPI;
import com.blue_unicorn.android_auth_lib.api.authenticator.BaseAuthenticatorAPI;
import com.blue_unicorn.android_auth_lib.fido.FidoObject;
import com.blue_unicorn.android_auth_lib.fido.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.request.RequestObject;
import com.blue_unicorn.android_auth_lib.fido.reponse.ResponseObject;

import io.reactivex.rxjava3.core.Single;

public class BaseAPIHandler implements APIHandler {

    private Context context;

    public BaseAPIHandler(Context context) {
        this.context = context;
    }

    public Single<FidoObject> callAPI(RequestObject request) {
        return Single.defer(() -> {
            AuthenticatorAPI api = new BaseAuthenticatorAPI(context, true);

            if (request instanceof MakeCredentialRequest) {
                return api.makeCredential((MakeCredentialRequest)request);
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
            AuthenticatorAPI api = new BaseAuthenticatorAPI(context, true);

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
