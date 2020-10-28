package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt.FidoGattProfile;
import com.nexenio.rxandroidbleserver.RxBleServer;
import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class BleHandler {

    private FidoGattProfile fidoGattProfile;
    private RxBleServer bleServer;
    private MutableLiveData<Throwable> errors;
    private int mtu;

    public BleHandler(@NonNull Context context, MutableLiveData<Throwable> fidoAuthServiceErrors) {
        this.fidoGattProfile = new FidoGattProfile(context);
        this.bleServer = fidoGattProfile.getGattServer();
        this.errors = fidoAuthServiceErrors;
        this.mtu = 20; // this is the default value on android (23 byte minus GATT overhead)
    }

    public void connect() {
        Timber.d("Triggering BLE advertising...");
        bleServer.observerClientMtu().subscribe(clientMtu -> mtu = clientMtu);
        bleServer.provideServicesAndAdvertise(FidoGattProfile.FIDO_SERVICE_UUID, FidoGattProfile.DEVICE_INFORMATION_SERVICE_UUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, this::postError);
    }

    public void disconnect() {
        Timber.d("Disconnecting clients from server..");
        for (RxBleClient client : bleServer.getClients()) {
            Timber.d("\tdisconnect client %s", client.getBluetoothDevice().getAddress());
            bleServer.disconnect(client);
        }
    }

    public void removeServices() {
        Timber.d("Removing offered services...");
        for(RxBleService service: bleServer.getServices()) {
            Timber.d("\tremove service %s", service.getUuid());
            bleServer.removeService(service);
        }
        //TODO => really "close" server => when / how is dispose triggered?
    }

    public Observable<byte[]> getIncomingBleData() {
        return fidoGattProfile.getFidoControlPointCharacteristic().getValueChanges().map(RxBleValue::getBytes);
    }

    public void sendBleData(byte[] data) {
        Timber.d("Write response of length %d to status characteristic", data.length);
        fidoGattProfile.getFidoStatusCharacteristic().setValue(new BaseValue(data)).doOnError(throwable -> {
            Timber.d(throwable, "Something went wrong");
        }).blockingAwait();
        Timber.d("\tGet Status Characteristic to send notification");
        fidoGattProfile.getFidoStatusCharacteristic().sendNotifications().doOnError(throwable -> {
            Timber.d(throwable, "Something went wrong");
        }).blockingAwait();
    }

    public int getMtu() {
        return mtu;
    }

    private void postError(@NonNull Throwable throwable) {
        Timber.w(throwable, "Something went wrong!");
        errors.postValue(throwable);
    }
}
