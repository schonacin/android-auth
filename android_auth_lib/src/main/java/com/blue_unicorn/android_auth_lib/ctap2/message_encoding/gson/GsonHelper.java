package com.blue_unicorn.android_auth_lib.ctap2.message_encoding.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public final class GsonHelper {

    private static final GsonHelper instance = new GsonHelper();

    public Gson byteArraytoJsonGson;

    private GsonHelper() {
        byteArraytoJsonGson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class,
                new ByteArrayToJSONTypeAdapter()).create();
    }

    public static GsonHelper getInstance() {
        return instance;
    }

}