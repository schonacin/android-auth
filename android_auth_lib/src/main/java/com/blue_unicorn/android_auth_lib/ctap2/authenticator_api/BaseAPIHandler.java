package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.blue_unicorn.android_auth_lib.android.constants.UserPreference;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.FidoObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;

import io.reactivex.rxjava3.core.Single;

public class BaseAPIHandler implements APIHandler {

    private AuthenticatorAPI api;
    private Context context;

    public BaseAPIHandler(Context context) {
        this.api = new BaseAuthenticatorAPI(context, true);
        this.context = context;
    }

    public Single<FidoObject> callAPI(RequestObject request) {
        return Single.defer(() -> {
            if (request instanceof MakeCredentialRequest) {
                return api.makeCredential((MakeCredentialRequest) request);
            } else if (request instanceof GetAssertionRequest) {
                return api.getAssertion((GetAssertionRequest) request);
            } else if (request instanceof GetInfoRequest) {
                // check for extension support if wanted
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (sharedPreferences.getBoolean(UserPreference.CONTINUOUS_AUTHENTICATION_SUPPORT, false)) {
                    return api.getInfoSetupExtension((GetInfoRequest) request);
                } else {
                    return api.getInfo((GetInfoRequest) request);
                }
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
            } else if (request instanceof GetInfoRequest) {
                return api.getInfo((GetInfoRequest) request);
            } else {
                return Single.error(new AuthLibException("invalid request object for updating api"));
            }
        });
    }
}
