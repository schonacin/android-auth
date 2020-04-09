package com.blue_unicorn.android_auth;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.blue_unicorn.android_auth_lib.android.FidoAuthService;
import com.blue_unicorn.android_auth_lib.android.FidoAuthServiceBinder;

// TODO: maybe make this part of lib?
public class FidoAuthServiceConnection implements ServiceConnection {

    private FidoAuthService mBoundService;

    public void onServiceConnected(ComponentName className, IBinder service) {
        // This is called when the connection with the service has been
        // established, giving us the service object we can use to
        // interact with the service. Because we have bound to a explicit
        // service that we know is running in our own process, we can
        // cast its IBinder to a concrete class and directly access it.
        mBoundService = ((FidoAuthServiceBinder) service).getService();

    }

    public void onServiceDisconnected(ComponentName className) {
        // This is called when the connection with the service has been
        // unexpectedly disconnected -- that is, its process crashed.
        // Because it is running in our same process, we should never
        // see this happen.
        mBoundService = null;
    }
}
