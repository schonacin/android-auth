package com.blue_unicorn.android_auth_lib.cbor;

import android.util.Pair;

import com.blue_unicorn.android_auth_lib.AuthLibException;
import com.google.gson.annotations.SerializedName;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

class CborSerializer {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    private static boolean isFinal(Object o) {
        return o.getClass().isPrimitive() || o.getClass().isArray() || o.getClass() == Integer.class || o.getClass() == String.class || o instanceof Map;
    }

    private static Single<Map<String, Object>> serializeInnerObject(Object o) {
        return Single.defer(() -> Single.just(o.getClass().getDeclaredFields())
                .flatMapPublisher(Flowable::fromArray)
                .concatMapMaybe(field -> {
                    field.setAccessible(true);
                    SerializedName annotation = field.getAnnotation(SerializedName.class);
                    if (annotation == null) {
                        return Maybe.empty();
                    }
                    Object value = field.get(o);
                    if (value == null) {
                        return Maybe.empty();
                    }
                    return Single.just(value)
                            .flatMap(val -> {
                                if (isFinal(val)) {
                                    return Single.just(val);
                                } else {
                                    return serializeInnerObject(val);
                                }
                            })
                            .map(val -> Pair.create(annotation.value(), val))
                            .toMaybe();
                })
                .toMap(x -> x.first, x -> x.second)).onErrorResumeNext(throwable -> Single.error(new AuthLibException("Could not serialize object!", throwable)));
    }

    static Single<byte[]> serialize(Object o) {
        return Single.defer(() -> Single.just(o.getClass().getDeclaredFields())
                .flatMapPublisher(Flowable::fromArray)
                .concatMapMaybe(field -> {
                    field.setAccessible(true);
                    SerializedIndex annotation = field.getAnnotation(SerializedIndex.class);
                    if (annotation == null) {
                        return Maybe.empty();
                    }
                    Object value = field.get(o);
                    if (value == null) {
                        return Maybe.empty();
                    }
                    return Single.just(value)
                            .flatMap(val -> {
                                if (isFinal(val)) {
                                    return Single.just(val);
                                } else {
                                    return serializeInnerObject(val);
                                }
                            })
                            .map(val -> Pair.create(annotation.value(), val))
                            .toMaybe();
                })
                .toMap(x -> x.first, x -> x.second)
                .map(result -> CBORObject.FromObject(result).EncodeToBytes(ENCODE_OPTIONS))).onErrorResumeNext(throwable -> Single.error(new AuthLibException("Could not serialize object!", throwable)));
    }

}
