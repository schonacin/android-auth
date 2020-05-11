package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.android.layers.APILayer;
import com.blue_unicorn.android_auth_lib.android.layers.RequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.ResponseLayer;

public class AuthHandler {

    private RequestLayer requestLayer;
    private APILayer apiLayer;
    private ResponseLayer responseLayer;

    AuthHandler(Context context) {
        this.requestLayer = new RequestLayer(this);
        this.apiLayer = new APILayer(this, context);
        this.responseLayer = new ResponseLayer();
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
}
