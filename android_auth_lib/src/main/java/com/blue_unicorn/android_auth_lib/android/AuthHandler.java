package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.android.layers.APILayer;
import com.blue_unicorn.android_auth_lib.android.layers.RequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.ResponseLayer;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;

public class AuthHandler {

    private RequestLayer requestLayer;
    private APILayer apiLayer;
    private ResponseLayer responseLayer;

    AuthHandler(Context context) {
        this.requestLayer = new RequestLayer(this);
        this.apiLayer = new APILayer(this, context);
        this.responseLayer = new ResponseLayer(this);
    }

    public RequestLayer getRequestLayer() {
        return requestLayer;
    }

    public APILayer getApiLayer() {
        return apiLayer;
    }

    public ResponseLayer getResponseLayer() {
        return responseLayer;
    }

    public int getMaxLength() {
        return 20;
    }

    public void initialize(Observable<byte[]> requests) {
        requestLayer.initialize(requests);
    }

    public Flowable<byte[]> getResponses() {
        return responseLayer.getResponses();
    }
}
