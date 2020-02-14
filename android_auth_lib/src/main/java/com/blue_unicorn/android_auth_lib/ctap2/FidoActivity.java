package com.blue_unicorn.android_auth_lib.ctap2;

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
