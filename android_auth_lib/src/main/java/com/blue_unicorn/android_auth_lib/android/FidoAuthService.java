package com.blue_unicorn.android_auth_lib.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class FidoAuthService extends Service {

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        /* TODO:
         * 1. check ble hardware availability/compatibility
         *      error message/exit if not
         * 2. check if ble is activated
         *      ask user to activate if not
         *          error message/exit if denied
         * 3. start advertising & build observable chains
         */
        Log.i("AndroidAuth", "onBind() called!\t" + intent);

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO: stop observable chain with onComplete()
        return false;
    }

    public class LocalBinder extends Binder {
        public FidoAuthService getService() {
            return FidoAuthService.this;
        }
    }
}
