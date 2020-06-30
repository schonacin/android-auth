package com.blue_unicorn.android_auth_lib.android.layers;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.android.AuthSubscriber;

import org.reactivestreams.Subscriber;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;

public class ResponseLayer {

    private AuthHandler authHandler;

    private DisposableSubscriber<byte[]> responseSubscriber;
    private PublishSubject<byte[]> subject;

    public ResponseLayer(AuthHandler authHandler) {
        this.authHandler = authHandler;
        subject = PublishSubject.create();
    }

    public Flowable<byte[]> getResponses() {
        // Note: this subject doesn't complete when the subscriber completes.
        // It sends out byte arrays whenever any response subscriber calls onNext()
        return subject
                .toFlowable(BackpressureStrategy.BUFFER);
    }

    public Subscriber<byte[]> getResponseSubscriber() {
        if (responseSubscriber != null && !responseSubscriber.isDisposed())
            this.responseSubscriber.dispose();
        return createNewResponseSubscriber();
    }

    private Subscriber<byte[]> createNewResponseSubscriber() {
        this.responseSubscriber = new AuthSubscriber<byte[]>(authHandler) {
            @Override
            public void onNext(byte[] response) {
                // fragments are sent back to bluetooth either via call here
                authHandler.getBleHandler().sendBleData(response);
                // OR via another Flowable with a Subject.
                subject.onNext(response);
                request(1);
            }

            @Override
            public void onComplete() {
                authHandler.getKeepaliveHandler().stopKeepalive();
            }
        };
        return responseSubscriber;
    }
}
