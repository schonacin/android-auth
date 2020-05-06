package com.blue_unicorn.android_auth_lib.android;

import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt.FidoGattProfile;
import com.nexenio.rxandroidbleserver.RxBleServer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class FidoAuthService extends Service {

    private final CompositeDisposable fidoAuthServiceDisposable = new CompositeDisposable();
    private FidoGattProfile fidoGattProfile;
    private RxBleServer bleServer;
    private Disposable provideAndAdvertiseServicesDisposable;
    private Disposable updateFidoStatusDisposable;

    private MutableLiveData<Boolean> isProvidingAndAdvertisingServices = new MutableLiveData<>();
    private MutableLiveData<Throwable> errors = new MutableLiveData<>();

    private final IBinder mBinder = new FidoAuthServiceBinder(this);

    @Override
    public IBinder onBind(Intent intent) {
        Timber.i("onBind() called!	%s", intent);

        if (!bleCapable()) {
            Timber.e("Device is not BLE capable");
            return null;
        }

        if (!bleEnabled()) {
            Timber.e("BLE is not enabled");
            return null;
        }

        fidoGattProfile = new FidoGattProfile(this);
        bleServer = fidoGattProfile.getGattServer();

        isProvidingAndAdvertisingServices.setValue(false);

        /* start advertising & build observable chains
         *
         * setForeground?
         */
        //fidoGattProfile.getFidoControlPointCharacteristic().getValueChanges();

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        fidoAuthServiceDisposable.dispose();
        // TODO: stop observable chain with onComplete()
        return false;
    }

    private boolean bleCapable() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private boolean bleEnabled() {
        return ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled();
    }

    public LiveData<Boolean> isProvidingAndAdvertisingServices() {
        return isProvidingAndAdvertisingServices;
    }

    public void toggleProvidingAndAdvertisingServices() {
        if (!isProvidingAndAdvertisingServices.getValue()) {
            startProvidingAndAdvertisingServices();
        } else {
            stopProvidingAndAdvertisingServices();
        }
    }

    private void startProvidingAndAdvertisingServices() {
        Timber.d("Starting to provide and advertise services");
        provideAndAdvertiseServicesDisposable = bleServer.provideServicesAndAdvertise(FidoGattProfile.FIDO_SERVICE_UUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isProvidingAndAdvertisingServices.postValue(true))
                .doFinally(() -> isProvidingAndAdvertisingServices.postValue(false))
                .subscribe(
                        () -> Timber.i("Stopped providing and advertising services"),
                        this::postError
                );
        updateFidoStatusDisposable = fidoGattProfile.updateFidoStatusCharacteristicValue()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Timber.d("Done updating characteristic values"),
                        this::postError
                );

        fidoAuthServiceDisposable.add(provideAndAdvertiseServicesDisposable);
        fidoAuthServiceDisposable.add(updateFidoStatusDisposable);
    }

    private void stopProvidingAndAdvertisingServices() {
        Timber.d("Stopping to provide and advertise services");
        if (provideAndAdvertiseServicesDisposable != null && !provideAndAdvertiseServicesDisposable.isDisposed()) {
            provideAndAdvertiseServicesDisposable.dispose();
        }
        /*if (updateFidoStatusDisposable != null && !updateFidoStatusDisposable.isDisposed()) {
            updateFidoStatusDisposable.dispose();
        }*/
    }

    private void postError(@NonNull Throwable throwable) {
        Timber.w(throwable);
        errors.postValue(throwable);
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }
}
