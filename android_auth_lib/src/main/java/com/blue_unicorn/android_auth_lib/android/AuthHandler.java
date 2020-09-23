package com.blue_unicorn.android_auth_lib.android;

import com.blue_unicorn.android_auth_lib.android.layers.APILayer;
import com.blue_unicorn.android_auth_lib.android.layers.RequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.ResponseLayer;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.BleHandler;

import io.reactivex.rxjava3.core.Flowable;

/**
 * Handles the different components to create the whole CTAP2 flow. Creates and manages the interaction between Request-, API- and Response layer.
 */
public interface AuthHandler {

    Class getActivityClass();

    RequestLayer getRequestLayer();

    APILayer getApiLayer();

    ResponseLayer getResponseLayer();

    BleHandler getBleHandler();

    NotificationHandler getNotificationHandler();

    void startAdvertisingProcess();

    void stopAdvertisingProcess();

    Flowable<byte[]> getResponses();
}
