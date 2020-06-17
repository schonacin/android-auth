package com.blue_unicorn.android_auth;

import android.app.Application;

import timber.log.Timber;

public class AndroidAuthApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

    }
}
