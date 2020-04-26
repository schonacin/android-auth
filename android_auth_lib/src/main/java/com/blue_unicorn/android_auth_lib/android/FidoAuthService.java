package com.blue_unicorn.android_auth_lib.android;

import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt.FidoGattProfile;
import com.nexenio.rxandroidbleserver.RxBleServer;

import java.util.UUID;

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
    private Disposable advertiseServicesDisposable;
    //private Disposable updateValueDisposable;

    private MutableLiveData<Boolean> isProvidingAndAdvertisingServices = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAdvertisingService = new MutableLiveData<>();
    private MutableLiveData<Throwable> errors = new MutableLiveData<>();

    private final IBinder mBinder = new FidoAuthServiceBinder(this);

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("AndroidAuth", "onBind() called!\t" + intent);

        if (!bleCapable()) {
            Log.e("FidoAuthService", "Device is not BLE capable");
            return null;
        }

        if (!bleEnabled()) {
            Log.e("FidoAuthService", "BLE is not enabled");
            return null;
        }

        fidoGattProfile = new FidoGattProfile(this);
        bleServer = fidoGattProfile.getGattServer();

        isProvidingAndAdvertisingServices.setValue(false);
        isAdvertisingService.setValue(false);

        /* start advertising & build observable chains
         *
         * setForeground?
         */

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
        Timber.d("Starting to provide services");
        provideAndAdvertiseServicesDisposable = bleServer.provideServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isProvidingAndAdvertisingServices.postValue(true))
                .doFinally(() -> isProvidingAndAdvertisingServices.postValue(false))
                .subscribe(
                        () -> Timber.i("Stopped providing services"),
                        this::postError
                );
        /*updateValueDisposable = fidoGattProfile.updateCharacteristicValues()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Timber.d("Done updating characteristic values"),
                        this::postError
                );*/
        Timber.d("Starting to advertise services");
        UUID uuid = FidoGattProfile.FIDO_SERVICE_UUID;
        advertiseServicesDisposable = bleServer.advertise(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isAdvertisingService.postValue(true))
                .doFinally(() -> isAdvertisingService.postValue(false))
                .subscribe(
                        () -> Timber.i("Stopped advertising services"),
                        this::postError
                );


        fidoAuthServiceDisposable.add(provideAndAdvertiseServicesDisposable);
        //viewModelDisposable.add(updateValueDisposable);
        fidoAuthServiceDisposable.add(advertiseServicesDisposable);
    }

    private void stopProvidingAndAdvertisingServices() {
        Timber.d("Stopping to advertise services");
        if (advertiseServicesDisposable != null && !advertiseServicesDisposable.isDisposed()) {
            advertiseServicesDisposable.dispose();
        }
        Timber.d("Stopping to provide services");
        if (provideAndAdvertiseServicesDisposable != null && !provideAndAdvertiseServicesDisposable.isDisposed()) {
            provideAndAdvertiseServicesDisposable.dispose();
        }
        /*if (updateValueDisposable != null && !updateValueDisposable.isDisposed()) {
            updateValueDisposable.dispose();
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
