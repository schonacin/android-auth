package com.blue_unicorn.android_auth;

import android.os.Handler;

import timber.log.Timber;

public class ContAuthMockClient extends Thread {

    private Handler handler;

    public ContAuthMockClient(Handler handler) {
        this.handler = handler;
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
}
