package com.blue_unicorn.android_auth_lib.android;

import android.content.ServiceConnection;

public interface FidoActivity {

    boolean mBound = false;

    FidoService mFidoService = null;

    ServiceConnection connection = null;

    void onStart();

    void onCreate();

    void startService();

    void stopService();

    void updateProcess(RequestObject object);

}
