package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.exceptions.InvalidRequestObjectException;
import com.blue_unicorn.android_auth_lib.fido.BaseGetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseGetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseMakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.FidoObject;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;
import com.blue_unicorn.android_auth_lib.fido.ResponseObject;

import io.reactivex.rxjava3.core.Observable;

public class BaseAPIHandler implements APIHandler {

    public Observable<FidoObject> callAPI(RequestObject request) {

            AuthenticatorAPI api = new BaseAuthenticatorAPI();

            if (request instanceof MakeCredentialRequest) {

                return api.makeCredential((BaseMakeCredentialRequest)request)
                        .switchMap(x -> Observable.just((FidoObject)x));

            } else if (request instanceof GetAssertionRequest) {

                return api.getAssertion((BaseGetAssertionRequest) request)
                        .switchMap(x -> Observable.just((FidoObject)x));

            } else if (request instanceof GetInfoRequest) {

                return api.getInfo((BaseGetInfoRequest) request)
                        .switchMap(x -> Observable.just((FidoObject)x));

            } else {
                return Observable.error(new InvalidRequestObjectException());
            }

    }

    public Observable<ResponseObject> updateAPI(RequestObject request) {

        AuthenticatorAPI api = new BaseAuthenticatorAPI();

        if (request instanceof MakeCredentialRequest) {

            return api.makeInternalCredential((BaseMakeCredentialRequest) request)
                    .switchMap(x -> Observable.just((ResponseObject) x));

        } else if (request instanceof GetAssertionRequest) {

            return api.getInternalAssertion((BaseGetAssertionRequest) request)
                    .switchMap(x -> Observable.just((ResponseObject) x));

        } else {

            return Observable.error(new InvalidRequestObjectException());

        }

    }

}
