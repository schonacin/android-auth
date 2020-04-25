package com.blue_unicorn.android_auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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

public class ViewModel extends AndroidViewModel {

    private final CompositeDisposable viewModelDisposable = new CompositeDisposable();

    private final FidoGattProfile fidoGattProfile;
    private RxBleServer bleServer;
    private Disposable provideServicesDisposable;
    private Disposable advertiseServicesDisposable;
    //private Disposable updateValueDisposable;

    private MutableLiveData<Boolean> isProvidingServices = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAdvertisingService = new MutableLiveData<>();
    private MutableLiveData<Throwable> errors = new MutableLiveData<>();

    public ViewModel(@NonNull Application application) {
        super(application);

        fidoGattProfile = new FidoGattProfile(application);
        bleServer = fidoGattProfile.getGattServer();

        isProvidingServices.setValue(false);
        isAdvertisingService.setValue(false);
    }

    @Override
    protected void onCleared() {
        viewModelDisposable.dispose();
        super.onCleared();
    }

    /*
        Providing services
     */

    public LiveData<Boolean> isProvidingServices() {
        return isProvidingServices;
    }

    public void toggleProvidingServices() {
        if (!isProvidingServices.getValue()) {
            startProvidingServices();
        } else {
            stopProvidingServices();
        }
    }

    private void startProvidingServices() {
        Timber.d("Starting to provide services");
        provideServicesDisposable = bleServer.provideServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isProvidingServices.postValue(true))
                .doFinally(() -> isProvidingServices.postValue(false))
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

        viewModelDisposable.add(provideServicesDisposable);
        //viewModelDisposable.add(updateValueDisposable);
    }

    private void stopProvidingServices() {
        Timber.d("Stopping to provide services");
        if (provideServicesDisposable != null && !provideServicesDisposable.isDisposed()) {
            provideServicesDisposable.dispose();
        }
        /*if (updateValueDisposable != null && !updateValueDisposable.isDisposed()) {
            updateValueDisposable.dispose();
        }*/
    }

    /*
        Advertising services
     */

    public LiveData<Boolean> isAdvertisingService() {
        return isAdvertisingService;
    }

    public void toggleAdvertisingServices() {
        if (!isAdvertisingService.getValue()) {
            startProvidingServices();
            startAdvertisingServices();
        } else {
            stopAdvertisingServices();
            stopProvidingServices();
        }
    }

    private void startAdvertisingServices() {
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

        viewModelDisposable.add(advertiseServicesDisposable);
    }

    private void stopAdvertisingServices() {
        Timber.d("Stopping to advertise services");
        if (advertiseServicesDisposable != null && !advertiseServicesDisposable.isDisposed()) {
            advertiseServicesDisposable.dispose();
        }
    }

    /*
        Errors
     */

    private void postError(@NonNull Throwable throwable) {
        Timber.w(throwable);
        errors.postValue(throwable);
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }
}
