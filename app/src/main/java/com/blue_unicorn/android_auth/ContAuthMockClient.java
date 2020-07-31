package com.blue_unicorn.android_auth;

import android.os.Handler;
import android.util.Base64;

import com.blue_unicorn.android_auth_lib.android.FidoAuthService;

import timber.log.Timber;

public class ContAuthMockClient extends Thread {

    private static final byte[] RAW_GET_INFO_REQUEST = new byte[]{(byte) 0x83, (byte) 0x00, (byte) 0x01, (byte) 0x04};
    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);
    private static final byte[] RAW_GET_ASSERTION_REQUEST_WITH_EXTENSION_PARAMETER = Base64.decode("AqQBa3dlYmF1dGhuLmlvAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEEoXgZY29udGludW91c19hdXRoZW50aWNhdGlvbhknEAWhYnV29Q==", Base64.DEFAULT);
    private static final byte[] RAW_GET_ASSERTION_REQUEST_WITH_EXTENSION_PARAMETER_AND_UV_BIT = Base64.decode("AqQBa3dlYmF1dGhuLmlvAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEEoXgZY29udGludW91c19hdXRoZW50aWNhdGlvbhknEAWiYnV29WJ1cPU=", Base64.DEFAULT);

    private Handler handler;
    private FidoAuthService fidoAuthService;

    public ContAuthMockClient(Handler handler, FidoAuthService fidoAuthService) {
        this.handler = handler;
        this.fidoAuthService = fidoAuthService;
    }

    @Override
    public void run() {
        try {
            while (true) {
                sleep(2000);
                Timber.d("JOJOJO WHADDUP");
                handler.post(this);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendGetInfoAndMakeCredential() {
        fidoAuthService.getAuthHandler().getResponseLayer().getSubject().subscribe();
        fidoAuthService.getAuthHandler().getApiLayer().buildNewRequestChain(RAW_GET_INFO_REQUEST);
        //fidoAuthService.getAuthHandler().getApiLayer().buildNewRequestChain(RAW_MAKE_CREDENTIAL_REQUEST);
    }

    public void startGetAssertionContinuous(int interval) {

    }

    public void stopGetAssertionContinuous() {

    }
}
