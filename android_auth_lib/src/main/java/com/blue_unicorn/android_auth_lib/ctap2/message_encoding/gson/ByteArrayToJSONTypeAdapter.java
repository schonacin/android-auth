package com.blue_unicorn.android_auth_lib.ctap2.message_encoding.gson;

import android.util.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nexenio.rxandroidbleserver.service.value.ValueUtil;

import java.lang.reflect.Type;

import timber.log.Timber;

public class ByteArrayToJSONTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Timber.d("Deserializing Json Element %s", json.getAsString());
        byte[] decode = Base64.decode(json.getAsString(), Base64.URL_SAFE);
        Timber.d("\t result: %s", ValueUtil.bytesToHex(decode));
        return Base64.decode(json.getAsString(), Base64.URL_SAFE);
    }

    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
        Timber.d("Serializing Json Element %s", ValueUtil.bytesToHex(src));
        JsonPrimitive jsonPrimitive = new JsonPrimitive(Base64.encodeToString(src, Base64.DEFAULT));
        Timber.d("\t result: %s", jsonPrimitive.getAsString());
        return jsonPrimitive;
    }
}
