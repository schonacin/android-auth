package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt.FidoGattProfile;
import com.nexenio.rxandroidbleserver.RxBleServer;
import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BleHandler {

    private FidoGattProfile fidoGattProfile;
    private RxBleServer bleServer;
    private MutableLiveData<Throwable> errors;

    public BleHandler(@NonNull Context context, MutableLiveData<Throwable> fidoAuthServiceErrors) {
        this.fidoGattProfile = new FidoGattProfile(context);
        this.bleServer = fidoGattProfile.getGattServer();
        this.errors = fidoAuthServiceErrors;
        Log.d("android-auth", "stating BLE advertising");
        bleServer.provideServicesAndAdvertise(FidoGattProfile.FIDO_SERVICE_UUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, this::postError);
    }

    public void disconnect() {
        for (RxBleClient client : bleServer.getClients())
            bleServer.disconnect(client);
    }

    public Observable<byte[]> getIncomingBleData() {
        return fidoGattProfile.getFidoControlPointCharacteristic().getValueChanges().map(RxBleValue::getBytes);
    }

    public void sendBleData(byte[] data) {
        fidoGattProfile.getFidoStatusCharacteristic().setValue(new BaseValue(data));
        fidoGattProfile.getFidoStatusCharacteristic().sendNotifications();
    }

    // TODO: make this dynamic, as client can change the MTU
    public int getMtu() {
        return 20;
    }

    private void postError(@NonNull Throwable throwable) {
        Log.w("android-auth", throwable);
        errors.postValue(throwable);
    }
}
