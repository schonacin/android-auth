package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt.FidoGattProfile;
import com.nexenio.rxandroidbleserver.RxBleServer;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class BleHandler {

    public final CompositeDisposable bleHandlerDisposable = new CompositeDisposable();
    private FidoGattProfile fidoGattProfile;
    private RxBleServer bleServer;
    private Disposable provideAndAdvertiseServicesDisposable;
    private Disposable updateFidoStatusDisposable;

    private MutableLiveData<Boolean> isProvidingAndAdvertisingServices = new MutableLiveData<>();
    private MutableLiveData<Throwable> errors;

    public BleHandler(@NonNull Context context, MutableLiveData<Throwable> fidoAuthServiceErrors) {
        this.fidoGattProfile = new FidoGattProfile(context);
        this.bleServer = fidoGattProfile.getGattServer();
        this.errors = fidoAuthServiceErrors;

        isProvidingAndAdvertisingServices.setValue(false);

    }

    public LiveData<Boolean> isProvidingAndAdvertisingServices() {
        return isProvidingAndAdvertisingServices;
    }

    public void startProvidingAndAdvertisingServices() {
        Timber.d("Starting to provide and advertise services");
        provideAndAdvertiseServicesDisposable = bleServer.provideServicesAndAdvertise(FidoGattProfile.FIDO_SERVICE_UUID/*, FidoGattProfile.DEVICE_INFORMATION_SERVICE_UUID*/)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isProvidingAndAdvertisingServices.postValue(true))
                .doFinally(() -> isProvidingAndAdvertisingServices.postValue(false))
                .subscribe(
                        () -> Timber.i("Stopped providing and advertising services"),
                        this::postError
                );
        /*
        updateFidoStatusDisposable = fidoGattProfile.updateFidoStatusCharacteristicValue()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Timber.d("Done updating characteristic values"),
                        this::postError
                );
        bleHandlerDisposable.add(updateFidoStatusDisposable);
        */
        bleHandlerDisposable.add(provideAndAdvertiseServicesDisposable);
    }

    public void stopProvidingAndAdvertisingServices() {
        Timber.d("Stopping to provide and advertise services");
        if (provideAndAdvertiseServicesDisposable != null && !provideAndAdvertiseServicesDisposable.isDisposed()) {
            provideAndAdvertiseServicesDisposable.dispose();
        }
        if (updateFidoStatusDisposable != null && !updateFidoStatusDisposable.isDisposed()) {
            updateFidoStatusDisposable.dispose();
        }
    }

    public Observable<RxBleValue> getIncomingBleData() {
        return fidoGattProfile.getFidoControlPointCharacteristic().getValueChanges();
    }

    public void sendBleData(byte[] data) {
        fidoGattProfile.setFidoStatusCharacteristicValue(new BaseValue(data));
        fidoGattProfile.sendFidoStatusCharacteristicNotifications();
    }

    // TODO: make this dynamic, as client can change the MTU
    public int getMtu() {
        return 20;
    }

    private void postError(@NonNull Throwable throwable) {
        Timber.w(throwable);
        errors.postValue(throwable);
    }
}
