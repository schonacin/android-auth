package com.blue_unicorn.android_auth_lib.android.layers;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.android.AuthSubscriber;
import com.nexenio.rxandroidbleserver.service.value.ValueUtil;

import org.reactivestreams.Subscriber;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;
import timber.log.Timber;

public class BaseResponseLayer implements ResponseLayer {

    private AuthHandler authHandler;

    private DisposableSubscriber<byte[]> responseSubscriber;
    private PublishSubject<byte[]> subject;

    public BaseResponseLayer(AuthHandler authHandler) {
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
        if (responseSubscriber != null && !responseSubscriber.isDisposed()) {
            responseSubscriber.dispose();
        }
        return createNewResponseSubscriber();
    }

    public void disposeChain() {
        if (responseSubscriber != null && !responseSubscriber.isDisposed()) {
            responseSubscriber.dispose();
        }
    }

    private Subscriber<byte[]> createNewResponseSubscriber() {
        responseSubscriber = new AuthSubscriber<byte[]>(authHandler) {
            @Override
            public void onNext(byte[] response) {
                // fragments are sent back to bluetooth either via call here
                Timber.d("Sent response of length %d", response.length);
                Timber.d("\t content: 0x%s", ValueUtil.bytesToHex(response));
                authHandler.getBleHandler().sendBleData(response);
                // OR via another Flowable with a Subject.
                subject.onNext(response);
                request(1);
            }

            @Override
            public void onComplete() {
                // this is called whenever a response chain ends.
                // Could be used for Keep Alive Purposes??
            }
        };
        return responseSubscriber;
    }
}
