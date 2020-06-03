package com.blue_unicorn.android_auth_lib.android;

import android.os.Binder;

public class FidoAuthServiceBinder extends Binder {

    private FidoAuthService fidoAuthService;

    public FidoAuthServiceBinder(FidoAuthService fidoAuthService) {
        super();
        this.fidoAuthService = fidoAuthService;
    }

    public FidoAuthService getService() {
        return fidoAuthService;
    }
}
