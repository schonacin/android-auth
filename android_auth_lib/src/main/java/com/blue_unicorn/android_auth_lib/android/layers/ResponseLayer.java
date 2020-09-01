package com.blue_unicorn.android_auth_lib.android.layers;

import org.reactivestreams.Subscriber;

import io.reactivex.rxjava3.core.Flowable;

/**
 * Handles returning the processed and fragmented responses
 */
public interface ResponseLayer {

    Flowable<byte[]> getResponses();

    Subscriber<byte[]> getResponseSubscriber();

    void disposeChain();

}
