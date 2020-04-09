package com.blue_unicorn.android_auth_lib.cbor;

import com.google.gson.annotations.SerializedName;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class CborSerializer {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    private static boolean isFinal(Object o) {
        return o.getClass().isPrimitive() || o.getClass().isArray() || o.getClass() == Integer.class || o.getClass() == String.class || o instanceof Map;
    }

    private static Map<String, Object> serializeInnerObject(Object o) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            SerializedName annotation = field.getAnnotation(SerializedName.class);
            if (annotation == null) {
                continue;
            }
            try {
                Object value = field.get(o);
                if (value == null) {
                    continue;
                }
                if (!isFinal(value)) {
                    value = serializeInnerObject(value);
                }
                result.put(annotation.value(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static byte[] serialize(Object o) {
        Map<Integer, Object> result = new LinkedHashMap<Integer, Object>();
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            SerializedIndex annotation = field.getAnnotation(SerializedIndex.class);
            if (annotation == null) {
                continue;
            }
            try {
                Object value = field.get(o);
                if (value == null) {
                    continue;
                }
                if (!isFinal(value)) {
                    value = serializeInnerObject(value);
                }
                result.put(annotation.value(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return CBORObject.FromObject(result).EncodeToBytes(ENCODE_OPTIONS);
    }

}
