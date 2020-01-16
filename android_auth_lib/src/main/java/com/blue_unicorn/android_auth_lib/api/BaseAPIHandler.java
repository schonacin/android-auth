package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.exceptions.InvalidRequestObjectException;
import com.blue_unicorn.android_auth_lib.fido.FidoObject;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;
import com.blue_unicorn.android_auth_lib.fido.ResponseObject;

import io.reactivex.rxjava3.core.Single;

public class BaseAPIHandler implements APIHandler {

    public Single<FidoObject> callAPI(RequestObject request) {

        return Single.defer(() -> {
            AuthenticatorAPI api = new BaseAuthenticatorAPI();

            if (request instanceof MakeCredentialRequest) {

                return api.makeCredential((MakeCredentialRequest)request);

            } else if (request instanceof GetAssertionRequest) {

                return api.getAssertion((GetAssertionRequest) request);

            } else if (request instanceof GetInfoRequest) {

                return api.getInfo((GetInfoRequest) request);

            } else {
                return Single.error(new InvalidRequestObjectException());
            }

        });

    }

    public Single<ResponseObject> updateAPI(RequestObject request) {

        return Single.defer(() -> {

            AuthenticatorAPI api = new BaseAuthenticatorAPI();

            if (request instanceof MakeCredentialRequest) {

                return api.makeInternalCredential((MakeCredentialRequest) request);

            } else if (request instanceof GetAssertionRequest) {

                return api.getInternalAssertion((GetAssertionRequest) request);

            } else {

                return Single.error(new InvalidRequestObjectException());

            }
        });
    }

}
