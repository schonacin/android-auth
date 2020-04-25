package com.blue_unicorn.android_auth_lib.android;

import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

public class FidoAuthService extends Service {

    private final IBinder mBinder = new FidoAuthServiceBinder(this);

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("AndroidAuth", "onBind() called!\t" + intent);

        if (!bleCapable()) {
            Log.e("FidoAuthService", "Device is not BLE capable");
            return null;
        }

        if (!bleEnabled()) {
            Log.e("FidoAuthService", "BLE is not enabled");
            return null;
        }

        /* start advertising & build observable chains
         *
         * setForeground?
         */


        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO: stop observable chain with onComplete()
        return false;
    }

    private boolean bleCapable() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private boolean bleEnabled() {
        return ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled();
    }
}
