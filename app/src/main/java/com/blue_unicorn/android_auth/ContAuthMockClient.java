package com.blue_unicorn.android_auth;

import android.os.Handler;
import android.util.Base64;

import com.blue_unicorn.android_auth_lib.android.FidoAuthService;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.CredentialSafe;

import org.reactivestreams.Subscriber;

import io.reactivex.rxjava3.subscribers.DisposableSubscriber;
import timber.log.Timber;

public class ContAuthMockClient {

    private static final byte[] RAW_GET_INFO_REQUEST = new byte[]{(byte) 0x04};
    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);
    private static final byte[] RAW_GET_ASSERTION_REQUEST_WITH_EXTENSION_PARAMETER = Base64.decode("AqQBa3dlYmF1dGhuLmlvAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEEoXgZY29udGludW91c19hdXRoZW50aWNhdGlvbhknEAWhYnV29Q==", Base64.DEFAULT);
    private static final byte[] RAW_GET_ASSERTION_REQUEST_WITH_EXTENSION_PARAMETER_AND_UV_BIT = Base64.decode("AqQBa3dlYmF1dGhuLmlvAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEEoXgZY29udGludW91c19hdXRoZW50aWNhdGlvbhknEAWiYnVw9WJ1dvU=", Base64.DEFAULT);
    private String lastCommand;
    private Handler handler;
    private FidoAuthService fidoAuthService;
    private int interval = 5000;
    private Boolean sendGetAssertionContinuously = true;
    final Runnable r = new Runnable() {
        public void run() {
            if (sendGetAssertionContinuously) {
                fidoAuthService.getAuthHandler().getApiLayer().buildNewRequestChain(RAW_GET_ASSERTION_REQUEST_WITH_EXTENSION_PARAMETER);
                handler.postDelayed(this, interval);
            } else return;
        }
    };

    public ContAuthMockClient(Handler handler, FidoAuthService fidoAuthService) {
        this.handler = handler;
        this.fidoAuthService = fidoAuthService;
        fidoAuthService.getAuthHandler().getBleHandler().setMtu(5000);
        fidoAuthService.getAuthHandler().getResponseLayer().getResponses().subscribe(getResponseSubscriber());
        resetKeystore(fidoAuthService);
    }

    public void resetKeystore(FidoAuthService fidoAuthService) {
        CredentialSafe credentialSafe = new CredentialSafe(fidoAuthService, true);
        credentialSafe.resetKeystore();
    }

    public void sendGetInfoAndMakeCredential() {
        lastCommand = "getInfo";
        fidoAuthService.getAuthHandler().getApiLayer().buildNewRequestChain(RAW_GET_INFO_REQUEST);
    }

    public void startGetAssertionContinuous(int interval) {
        setInterval(interval);
        lastCommand = "getAssertionWithUV";
        fidoAuthService.getAuthHandler().getApiLayer().buildNewRequestChain(RAW_GET_ASSERTION_REQUEST_WITH_EXTENSION_PARAMETER);
    }

    private Subscriber<byte[]> getResponseSubscriber() {
        return new DisposableSubscriber<byte[]>() {
            @Override
            public void onNext(byte[] response) {
                if (response[0] == (byte) 0x83 && response[3] == (byte) 0x00) {
                    Timber.d("RESPONSE WAS SUCCESSFUL, TRIGGERING NEXT REQUEST");
                    switch (lastCommand) {
                        case "getInfo":
                            lastCommand = "makeCredential";
                            Timber.d("Next request is: %s", lastCommand);
                            fidoAuthService.getAuthHandler().getApiLayer().buildNewRequestChain(RAW_MAKE_CREDENTIAL_REQUEST);
                            break;
                        case "makeCredential":
                            lastCommand = "getAssertionWithUV";
                            Timber.d("Next request is: %s", lastCommand);
                            fidoAuthService.getAuthHandler().getApiLayer().buildNewRequestChain(RAW_GET_ASSERTION_REQUEST_WITH_EXTENSION_PARAMETER_AND_UV_BIT);
                            break;
                        case "getAssertionWithUV":
                            lastCommand = "getAssertionWithoutUV";
                            Timber.d("Next request is: %s", lastCommand);
                            handler.post(r);
                            break;
                        case "getAssertionWithoutUV":
                            Timber.d("Next request is: %s", lastCommand);
                            break;
                        default:
                            break;
                    }
                } else {
                    Timber.d("RESPONSE CONTAINED ERROR");
                    sendGetAssertionContinuously = false;
                }
            }

            @Override
            public void onError(Throwable t) {
                Timber.d("Response subscriber error");
            }

            @Override
            public void onComplete() {
                Timber.d("Response subscriber compelete");
            }
        };
    }

    public void stopGetAssertionContinuous() {
    }

    private void setInterval(int interval) {
        this.interval = interval;
    }
}
