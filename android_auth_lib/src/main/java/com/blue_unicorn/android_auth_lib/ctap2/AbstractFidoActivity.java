package com.blue_unicorn.android_auth_lib.ctap2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public abstract class AbstractFidoActivity extends android.app.Activity implements FidoActivity{
    FidoService mFidoService;
    boolean mBound = false;

    public void onStart() {
        super.onStart();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void startService() {
        //bind to FidoService
        Intent intent = new Intent(this, FidoService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        //unbind from FidoService
        unbindService(connection);
        mBound = false;
    }

    public void updateProcess(RequestObject request) {
        // call method from FidoService only when bound
        if (mBound) {
            mFidoService.updateProcess(request);
        }
    }

    /** Defines callbacks for service binding */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            FidoService.LocalBinder binder = (FidoService.LocalBinder) service;
            mFidoService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
