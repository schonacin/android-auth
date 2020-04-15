package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.annotation.SerializedIndex;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;
import com.google.gson.annotations.SerializedName;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import java.lang.reflect.Field;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

final class CborSerializer {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    /*
     * Serializes Response Objects.
     * Transformes Objects to Maps (nested objects are transformed as well)
     * which get encoded to a CBOR Canonical Byte Array based on the CTAP2 specification
     *
     * */

    private CborSerializer() {
    }

    @NonNull
    static Single<byte[]> serialize(Object o) {
        return getObjectFields(o)
                .concatMapMaybe(field -> Maybe.zip(getMapKeyAsInteger(field), getMapValue(field, o), Pair::create))
                .toMap(x -> x.first, x -> x.second)
                .map(result -> CBORObject.FromObject(result).EncodeToBytes(ENCODE_OPTIONS))
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("Could not serialize object!", throwable)));
    }

    private static Single<Map<String, Object>> serializeInnerObject(Object o) {
        return getObjectFields(o)
                .concatMapMaybe(field -> Maybe.zip(getMapKeyAsString(field), getMapValue(field, o), Pair::create))
                .toMap(x -> x.first, x -> x.second)
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("Could not serialize object!", throwable)));
    }

    private static boolean isFinal(Object o) {
        return o.getClass().isPrimitive() || o.getClass().isArray() || o.getClass() == Integer.class || o.getClass() == String.class || o instanceof Map;
    }

    private static Flowable<Field> getObjectFields(Object o) {
        return Single.fromCallable(() -> o.getClass().getDeclaredFields())
                .flatMapPublisher(Flowable::fromArray);
    }

    private static Maybe<Object> getMapValue(Field field, Object o) {
        return Maybe.defer(() -> {
            field.setAccessible(true);
            Object fieldValue = field.get(o);
            if (fieldValue == null) {
                return Maybe.empty();
            }
            return Maybe.just(fieldValue)
                    .flatMapSingle(value -> {
                        if (isFinal(value)) {
                            return Single.just(value);
                        } else {
                            return serializeInnerObject(value);
                        }
                    });
        });
    }

    private static Maybe<String> getMapKeyAsString(Field field) {
        return Maybe.fromCallable(() -> field.getAnnotation(SerializedName.class))
                .flatMap(annotation -> {
                    if (annotation == null) {
                        return Maybe.empty();
                    } else {
                        return Maybe.just(annotation.value());
                    }
                });
    }

    private static Maybe<Integer> getMapKeyAsInteger(Field field) {
        return Maybe.fromCallable(() -> field.getAnnotation(SerializedIndex.class))
                .flatMap(annotation -> {
                    if (annotation == null) {
                        return Maybe.empty();
                    } else {
                        return Maybe.just(annotation.value());
                    }
                });
    }

}
