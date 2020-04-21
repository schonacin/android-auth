package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.annotations.SerializedIndex;
import com.google.gson.annotations.SerializedName;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

final class CborSerializer {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    /*
     * Serializes Response Objects.
     * Transforms Objects to Maps (nested objects are transformed as well)
     * which get encoded to a CBOR Canonical Byte Array based on the CTAP2 specification
     *
     * */

    private CborSerializer() {
    }

    @NonNull
    static Single<byte[]> serialize(ResponseObject o) {
        return getObjectFields(o)
                .concatMapMaybe(field -> Maybe.zip(getMapKeyAsInteger(field), getMapValue(field, o), Pair::create))
                .toMap(x -> x.first, x -> x.second)
                .map(result -> CBORObject.FromObject(result).EncodeToBytes(ENCODE_OPTIONS))
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("Could not serialize object!", throwable)));
    }

    @NonNull
    static Single<byte[]> serializeOther(Object o) {
        return processObject(o)
                .toSingle()
                .map(result -> CBORObject.FromObject(result).EncodeToBytes(ENCODE_OPTIONS))
                .onErrorResumeNext(throwable -> Single.error(new AuthLibException("Could not serialize object!", throwable)));
    }

    private static Maybe<Map<String, Object>> serializeInner(Object o) {
        return getObjectFields(o)
                .concatMapMaybe(field -> Maybe.zip(getMapKeyAsString(field), getMapValue(field, o), Pair::create))
                .toMap(x -> x.first, x -> x.second)
                .filter(map -> !map.isEmpty());
    }

    private static Maybe<List<Object>> serializeIterable(List<?> o) {
        return Maybe.fromCallable(() -> o)
                .flatMapPublisher(Flowable::fromIterable)
                .flatMapMaybe(CborSerializer::processObject)
                .toList()
                .filter(list -> !list.isEmpty());
    }

    private static Maybe<Object> processObject(Object o) {
        return Maybe.fromCallable(() -> o)
                .flatMap(value -> {
                    if (isFinal(value)) {
                        return Maybe.just(value);
                    } else if (isIterable(value)) {
                        return serializeIterable((List<?>)value);
                    } else {
                        return serializeInner(value);
                    }
                });
    }

    private static boolean isFinal(Object o) {
        // Assumptions:
        // Maps are final and can get serialized as is
        // Arrays just contain simple non Fido classes that do not need to be further serialized
        return o.getClass().isPrimitive() ||
                o.getClass().isArray() ||
                o.getClass() == Integer.class ||
                o.getClass() == String.class ||
                o instanceof Map<?,?>;
    }

    private static boolean isIterable(Object o) {
        // Assumption:
        // Lists contain dedicated Fido classes that need to be serialized
        return o instanceof List<?>;
    }

    private static Observable<Field> getObjectFields(Object o) {
        return Single.fromCallable(() -> o.getClass().getDeclaredFields())
                .flatMapObservable(Observable::fromArray);
    }

    private static Maybe<Object> getMapValue(Field field, Object o) {
        return Maybe.defer(() -> {
            field.setAccessible(true);
            Object fieldValue = field.get(o);
            return processObject(fieldValue);
        });
    }

    private static Maybe<String> getMapKeyAsString(Field field) {
        return Maybe.fromCallable(() -> field.getAnnotation(SerializedName.class))
                .map(SerializedName::value);
    }

    private static Maybe<Integer> getMapKeyAsInteger(Field field) {
        return Maybe.fromCallable(() -> field.getAnnotation(SerializedIndex.class))
                .map(SerializedIndex::value);
    }

}
