package com.blue_unicorn.android_auth_lib.ctap2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.core.Observable;

public class FidoService extends Service {

    static PublishSubject<byte[]> requestEmitter;
    static PublishSubject<RequestObject> userInputEmitter;
    static PublishSubject<FidoObject> apiEmitter;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        FidoService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FidoService.this;
        }
    }

    private void buildRequestChain() {

    }

    private void buildUpdateChain() {

    }

    public static Observable<RequestObject> getObservable() {

        //either build observable chains here or in onBind()

        return apiEmitter
                .ofType(RequestObject.class);
    }

    @Override
    public IBinder onBind(Intent intent) {

        // start advertising + build observable chains
        requestEmitter = PublishSubject.create();
        userInputEmitter = PublishSubject.create();

        return binder;

    }

    @Override
    public boolean onUnbind(Intent intent) {
        // stop observable chain here with onComplete()

        // no need to call onRebind()
        return false;
    }

    public void updateProcess(RequestObject request) {

        // handle updates of processes here, e. g. Registration was approved
        userInputEmitter.onNext(request);
    }

}
