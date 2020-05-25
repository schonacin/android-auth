package com.blue_unicorn.android_auth_lib.android.layers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.android.AuthSingleObserver;
import com.blue_unicorn.android_auth_lib.android.constants.IntentAction;
import com.blue_unicorn.android_auth_lib.android.constants.UserAction;
import com.blue_unicorn.android_auth_lib.android.constants.UserPreference;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.APIHandler;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.BaseAPIHandler;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.FidoObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.OtherException;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.BaseCborHandler;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.CborHandler;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;

public class APILayer {

    private Context context;

    private AuthHandler authHandler;

    private CborHandler cborHandler;
    private APIHandler apiHandler;
    private RxFragmentationProvider fragmentationProvider;

    // TODO: this implementation supports one bluetooth client at a time, do we need to handle this?
    // it will yield errors when new requests come in before old ones are processed
    private RequestObject request;

    public APILayer(AuthHandler authHandler, Context context) {
        this.context = context;
        this.authHandler = authHandler;
        this.cborHandler = new BaseCborHandler();
        this.apiHandler = new BaseAPIHandler(context);
        this.fragmentationProvider = new BaseFragmentationProvider();
    }

    void buildNewRequestChain(byte[] input) {
        SingleObserver<FidoObject> apiSubscriber = new AuthSingleObserver<FidoObject>(authHandler) {
            @Override
            public void onSuccess(FidoObject result) {
                if (result instanceof ResponseObject) {
                    buildResponseChainWithoutUserInteraction((ResponseObject) result);
                } else {
                    handleUserAction((RequestObject) result);
                }
            }
        };

        cborHandler.decode(input)
                .flatMap(apiHandler::callAPI)
                .subscribe(apiSubscriber);
    }

    private RequestObject getRequest() {
        if (request == null) {
            ErrorLayer.handleErrors(authHandler, new OtherException());
        }
        RequestObject requestInstance = request;
        this.request = null;
        return requestInstance;
    }

    private boolean setNewRequest(RequestObject request) {
        if (this.request != null) {
            // if the request isn't null, the prior request wasn't processed
            this.request = null;
            ErrorLayer.handleErrors(authHandler, new OtherException());
            return false;
        }
        this.request = request;
        return true;
    }

    private void handleUserAction(RequestObject request) {
        if (!setNewRequest(request)) {
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        @UserAction int userAction = 0;
        if (request instanceof MakeCredentialRequest) {
            userAction = sharedPreferences.getInt(UserPreference.MAKE_CREDENTIAL, UserAction.BUILD_NOTIFICATION_AND_PERFORM_AUTHENTICATION);
        } else if (request instanceof GetAssertionRequest) {
            userAction = sharedPreferences.getInt(UserPreference.GET_ASSERTION, UserAction.PERFORM_AUTHENTICATION);
        }

        switch (userAction) {
            case UserAction.BUILD_NOTIFICATION:
                buildNotification(request, false);
                break;
            case UserAction.BUILD_NOTIFICATION_AND_PERFORM_AUTHENTICATION:
                buildNotification(request, true);
                break;
            case UserAction.PERFORM_AUTHENTICATION:
                performAuthentication();
                break;
            case UserAction.PROCEED_WITHOUT_USER_INTERACTION:
                proceedWithoutUserInteraction();
                break;
        }
    }

    private void buildResponseChainWithoutUserInteraction(ResponseObject response) {
        // this chain will be built if there is no user interaction required
        cborHandler.encode(response)
                .map(BaseFrame::new)
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), authHandler.getMaxLength()))
                .map(Fragment::asBytes)
                .subscribe(authHandler.getResponseLayer().getResponseSubscriber());
    }

    private void buildResponseChainAfterUserInteraction(boolean isApproved) {
        // this chain is called after the user has interacted with the device
        // this function can be called from outside, i. e. a new intent on a service
        RequestObject requestInstance = getRequest();
        if (requestInstance == null) {
            return;
        }

        requestInstance.setApproved(isApproved);
        apiHandler.updateAPI(requestInstance)
                .flatMap(cborHandler::encode)
                .map(BaseFrame::new)
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), authHandler.getMaxLength()))
                .map(Fragment::asBytes)
                .subscribe(authHandler.getResponseLayer().getResponseSubscriber());
    }

    private void buildNotification(RequestObject request, boolean authenticationRequired) {
        // builds a notification with info on username/ Website
        // acceptance of notification should call buildResponseChainAfterUserInteraction()/
        // performAuthentication() based on the input parameter
    }

    private void performAuthentication() {
        // starts Activity responsible for authentication mechanism
        // Other possibilities to inject App behaviour into Lib could be:
        // @Override methods or Callbacks
        // Intent is implicit as we don't know the activity which performs this
        Intent intent = new Intent(IntentAction.CTAP_PERFORM_AUTHENTICATION);
        context.startActivity(intent);
    }

    private void proceedWithoutUserInteraction() {
        // this case should probably be non existent and might be removed later
        // Might be nice for testing purposes however
        buildResponseChainAfterUserInteraction(true);
    }
}
