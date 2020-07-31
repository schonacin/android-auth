package com.blue_unicorn.android_auth_lib.android.layers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.android.AuthSingleObserver;
import com.blue_unicorn.android_auth_lib.android.authentication.AuthInfo;
import com.blue_unicorn.android_auth_lib.android.authentication.AuthenticationAPICallback;
import com.blue_unicorn.android_auth_lib.android.authentication.BiometricAuth;
import com.blue_unicorn.android_auth_lib.android.constants.AuthenticationMethod;
import com.blue_unicorn.android_auth_lib.android.constants.IntentAction;
import com.blue_unicorn.android_auth_lib.android.constants.IntentExtra;
import com.blue_unicorn.android_auth_lib.android.constants.UserAction;
import com.blue_unicorn.android_auth_lib.android.constants.UserPreference;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.APIHandler;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.BaseAPIHandler;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.ExtensionSupport;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.FidoObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetInfoRequest;
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
import timber.log.Timber;

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
                Timber.d("Got Result from API Call: %s", result.getClass().getCanonicalName());
                if (result instanceof ResponseObject) {
                    Timber.d("\tis response, directly return without user interaction");
                    buildResponseChainWithoutUserInteraction((ResponseObject) result);
                } else {
                    Timber.d("\tis still request, handle respective user action!");
                    handleIntermediate((RequestObject) result);
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
        Timber.d("Getting existing request %s", requestInstance);
        if (requestInstance instanceof GetAssertionRequest) {
            Timber.d("\tAmount of selected Credentials: %s", ((GetAssertionRequest) requestInstance).getSelectedCredentials().size());
        }
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

    private void handleIntermediate(RequestObject request) {
        Timber.d("Handle user interaction");
        if (!setNewRequest(request)) {
            return;
        }
        if (request instanceof GetInfoRequest) {
            handleGetInfoExtensionSupport();
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        @UserAction int userAction = 0;
        Integer freshness = null;
        if (request instanceof MakeCredentialRequest) {
            userAction = sharedPreferences.getInt(UserPreference.MAKE_CREDENTIAL, UserAction.PERFORM_STANDARD_AUTHENTICATION);
        } else if (request instanceof GetAssertionRequest) {
            GetAssertionRequest getAssertionRequest = (GetAssertionRequest) request;
            if (getAssertionRequest.getContinuousFreshness() == null) {
                userAction = sharedPreferences.getInt(UserPreference.GET_ASSERTION, UserAction.PERFORM_STANDARD_AUTHENTICATION);
            } else {
                freshness = getAssertionRequest.getContinuousFreshness();
                Boolean isInitialRequest = getAssertionRequest.getOptions().get("up");
                if (isInitialRequest != null && isInitialRequest) {
                    userAction = UserAction.BUILD_NOTIFICATION_AND_PERFORM_AUTHENTICATION;
                } else {
                    userAction = UserAction.PERFORM_AUTHENTICATION;
                }
            }
        }

        switch (userAction) {
            case UserAction.BUILD_NOTIFICATION:
                Timber.d("\tbuild notification!");
                buildNotification(request, false, freshness);
                break;
            case UserAction.PERFORM_STANDARD_AUTHENTICATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Timber.d("\tperform standard android notification!");
                    performStandardAuthentication(request);
                    break;
                }
            case UserAction.BUILD_NOTIFICATION_AND_PERFORM_AUTHENTICATION:
                Timber.d("\tbuild notification and perform authentication!");
                buildNotification(request, true, freshness);
                break;
            case UserAction.PERFORM_AUTHENTICATION:
                Timber.d("\tperform authentication!");
                performCustomAuthentication(freshness);
                break;
            case UserAction.PROCEED_WITHOUT_USER_INTERACTION:
                Timber.d("\tproceed without any user interaction!");
                proceedWithoutUserInteraction();
                break;
            default:
                Timber.d("\tuser interaction unknown!");
                break;
        }
    }

    private void buildResponseChainWithoutUserInteraction(ResponseObject response) {
        // this chain will be built if there is no user interaction required
        cborHandler.encode(response)
                .map(BaseFrame::new)
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), authHandler.getBleHandler().getMtu()))
                .map(Fragment::asBytes)
                .subscribe(authHandler.getResponseLayer().getResponseSubscriber());
    }

    public void updateAfterUserInteraction(boolean isApproved) {
        // this method is called after the user has interacted with the device
        updateAfterUserInteraction(isApproved, null);
    }

    public void updateAfterUserInteraction(boolean isApproved, @Nullable Integer freshness) {
        // this method is called after the user has interacted with the device
        RequestObject requestInstance = getRequest();
        if (requestInstance == null) {
            authHandler.getNotificationHandler().notifyFailure();
            return;
        }
        authHandler.getNotificationHandler().notifyResult(new AuthInfo(requestInstance), isApproved);
        requestInstance.setApproved(isApproved);

        if (requestInstance instanceof GetAssertionRequest && isApproved && freshness != null) {
            ((GetAssertionRequest) requestInstance).setContinuousFreshness(freshness);
        }
        if (requestInstance instanceof GetInfoRequest && isApproved) {
            ((GetInfoRequest) requestInstance).setExtensionSupport(new ExtensionSupport());
        }

        apiHandler.updateAPI(requestInstance)
                .flatMap(cborHandler::encode)
                .map(BaseFrame::new)
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), authHandler.getBleHandler().getMtu()))
                .map(Fragment::asBytes)
                .doOnError(throwable -> Timber.d(throwable, "Something went wrong during update of api"))
                .subscribe(authHandler.getResponseLayer().getResponseSubscriber());
    }

    private void buildNotification(RequestObject request, boolean authenticationRequired, @Nullable Integer freshness) {
        // builds a notification with info on username/ Website
        // acceptance of notification should call buildResponseChainAfterUserInteraction()/
        // performAuthentication() based on the input parameter
        authHandler.getNotificationHandler().requestApproval(new AuthInfo(request), authenticationRequired, freshness);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void performStandardAuthentication(RequestObject request) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        @AuthenticationMethod int authenticationMethod = sharedPreferences.getInt(UserPreference.AUTHENTICATION_METHOD, AuthenticationMethod.FINGERPRINT);

        AuthenticationAPICallback authenticationCallback = new AuthenticationAPICallback() {
            @Override
            public void handleAuthentication(boolean authenticated) {
                updateAfterUserInteraction(authenticated);
            }
        };

        switch (authenticationMethod) {
            case AuthenticationMethod.FINGERPRINT_WITH_FALLBACK:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    BiometricAuth.authenticateWithCredentialFallback(context, new AuthInfo(request), authenticationCallback);
                    break;
                }
            case AuthenticationMethod.FINGERPRINT: {
                BiometricAuth.authenticate(context, new AuthInfo(request), authenticationCallback);
                break;
            }
        }
    }

    private void performCustomAuthentication(@Nullable Integer freshness) {
        // starts Activity responsible for authentication mechanism
        // Other possibilities to inject App behaviour into Lib could be:
        // @Override methods or Callbacks
        Intent intent = new Intent(context, authHandler.getActivityClass());
        intent.setAction(IntentAction.CTAP_PERFORM_AUTHENTICATION);
        if (freshness != null) {
            intent.putExtra(IntentExtra.AUTHENTICATION_FRESHNESS, freshness);
        }
        // TODO: figure out best way to send intent as startActivity requires an explicit flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

    private void handleGetInfoExtensionSupport() {
        Intent intent = new Intent(context, authHandler.getActivityClass());
        intent.setAction(IntentAction.CONTINUOUS_AUTHENTICATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

    private void proceedWithoutUserInteraction() {
        // this case should probably be non existent and might be removed later
        // Might be nice for testing purposes however
        updateAfterUserInteraction(true);
    }
}
