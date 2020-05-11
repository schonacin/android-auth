package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.android.layers.APILayer;
import com.blue_unicorn.android_auth_lib.android.layers.RequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.ResponseLayer;

import java.util.List;

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

    public void startUp(Observable<byte[]> requests) {
        requestLayer.initialize(requests);
    }

    public void constructResult(List<byte[]> result) {
        byte[] a = result.get(0);
    }
}
