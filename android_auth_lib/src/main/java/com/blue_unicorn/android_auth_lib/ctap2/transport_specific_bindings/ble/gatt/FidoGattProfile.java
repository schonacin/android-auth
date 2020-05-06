package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt;

import android.content.Context;

import androidx.annotation.NonNull;

import com.nexenio.rxandroidbleserver.RxBleServer;
import com.nexenio.rxandroidbleserver.RxBleServerProvider;
import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.ServiceBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.CharacteristicBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public final class FidoGattProfile {

    public static final UUID FIDO_SERVICE_UUID =
            UUID.fromString("0000FFFD-0000-1000-8000-00805F9B34FB");
    private static final UUID FIDO_CONTROL_POINT_CHARACTERISTIC_UUID =
            UUID.fromString("F1D0FFF1-DEAA-ECEE-B42F-C9BA7ED623BB");
    private static final UUID FIDO_STATUS_CHARACTERISTIC_UUID =
            UUID.fromString("F1D0FFF2-DEAA-ECEE-B42F-C9BA7ED623BB");
    private static final UUID FIDO_CONTROL_POINT_LENGTH_CHARACTERISTIC_UUID =
            UUID.fromString("F1D0FFF3-DEAA-ECEE-B42F-C9BA7ED623BB");
    private static final UUID FIDO_SERVICE_REVISION_BITFIELD_CHARACTERISTIC_UUID =
            UUID.fromString("F1D0FFF4-DEAA-ECEE-B42F-C9BA7ED623BB");

    private static final UUID DEVICE_INFORMATION_SERVICE_UUID =
            UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB");
    private static final UUID MANUFACTURER_NAME_STRING_CHARACTERISTIC_UUID =
            UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB");
    private static final UUID MODEL_NUMBER_STRING_CHARACTERISTIC_UUID =
            UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB");
    private static final UUID FIRMWARE_REVISION_STRING_CHARACTERISTIC_UUID =
            UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB");

    private RxBleServer gattServer;

    private RxBleService fidoService;
    private RxBleCharacteristic fidoControlPointCharacteristic;
    private RxBleCharacteristic fidoStatusCharacteristic;
    private RxBleCharacteristic fidoControlPointLengthCharacteristic;
    private RxBleCharacteristic fidoServiceRevisionBitfieldCharacteristic;

    private RxBleService deviceInformationService;
    private RxBleCharacteristic manufacturerNameStringCharacteristic;
    private RxBleCharacteristic modelNumberStringCharacteristic;
    private RxBleCharacteristic firmwareRevisionStringCharacteristic;

    public FidoGattProfile(@NonNull Context context) {
        createFidoGattServer(context);
    }

    // Example how to update the fido status characteristic
    public Completable updateFidoStatusCharacteristicValue() {
        return Observable.interval(1, TimeUnit.SECONDS)
                .map(count -> (int) (count % 42))
                .map(number -> new BaseValue(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(number).array()))
                .flatMapCompletable(value -> fidoStatusCharacteristic.setValue(value)
                        .andThen(fidoStatusCharacteristic.sendNotifications()));
    }

    private void createFidoGattServer(@NonNull Context context) {
        gattServer = RxBleServerProvider.createServer(context);
        gattServer.addService(createFidoService()).blockingAwait();
        gattServer.addService(createDeviceInformationService()).blockingAwait();
    }

    private RxBleService createFidoService() {
        fidoService = new ServiceBuilder(FIDO_SERVICE_UUID)
                .withCharacteristic(createFidoControlPointCharacteristic())
                .withCharacteristic(createFidoStatusCharacteristic())
                .withCharacteristic(createFidoControlPointLengthCharacteristic())
                .withCharacteristic(createFidoServiceRevisionBitfieldCharacteristic())
                .isPrimaryService()
                .build();

        return fidoService;
    }

    private RxBleCharacteristic createFidoControlPointCharacteristic() {
        fidoControlPointCharacteristic = new CharacteristicBuilder(FIDO_CONTROL_POINT_CHARACTERISTIC_UUID)
                .allowMitmProtectedEncryptedWrite()
                .supportWrites()
                .build();

        return fidoControlPointCharacteristic;
    }

    private RxBleCharacteristic createFidoStatusCharacteristic() {
        fidoStatusCharacteristic = new CharacteristicBuilder(FIDO_STATUS_CHARACTERISTIC_UUID)
                .supportNotifications()
                .build();

        return fidoStatusCharacteristic;
    }

    // TODO: set and update FidoControlPointLength depending on att_mtu
    private RxBleCharacteristic createFidoControlPointLengthCharacteristic() {
        fidoControlPointLengthCharacteristic = new CharacteristicBuilder(FIDO_CONTROL_POINT_LENGTH_CHARACTERISTIC_UUID)
                .withInitialValue(new BaseValue(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(512).array()))
                .allowMitmProtectedEncryptedRead()
                .supportReads()
                .build();

        return fidoControlPointLengthCharacteristic;
    }

    private RxBleCharacteristic createFidoServiceRevisionBitfieldCharacteristic() {
        fidoServiceRevisionBitfieldCharacteristic = new CharacteristicBuilder(FIDO_SERVICE_REVISION_BITFIELD_CHARACTERISTIC_UUID)
                .withInitialValue(new BaseValue(new byte[]{0x20}))
                .allowMitmProtectedEncryptedRead()
                .allowMitmProtectedEncryptedWrite()
                .supportReads()
                .supportWrites()
                .build();

        return fidoServiceRevisionBitfieldCharacteristic;
    }

    private RxBleService createDeviceInformationService() {
        deviceInformationService = new ServiceBuilder(DEVICE_INFORMATION_SERVICE_UUID)
                .withCharacteristic(createManufacturerNameStringCharacteristic())
                .withCharacteristic(createModelNumberStringCharacteristic())
                .withCharacteristic(createFirmwareRevisionStringCharacteristic())
                .isPrimaryService()
                .build();

        return deviceInformationService;
    }

    private RxBleCharacteristic createManufacturerNameStringCharacteristic() {
        manufacturerNameStringCharacteristic = new CharacteristicBuilder(MANUFACTURER_NAME_STRING_CHARACTERISTIC_UUID)
                .withInitialValue(new BaseValue("Test Manufacturer String".getBytes()))
                .allowMitmProtectedEncryptedRead()
                .supportReads()
                .build();

        return manufacturerNameStringCharacteristic;
    }

    private RxBleCharacteristic createModelNumberStringCharacteristic() {
        modelNumberStringCharacteristic = new CharacteristicBuilder(MODEL_NUMBER_STRING_CHARACTERISTIC_UUID)
                .withInitialValue(new BaseValue("Test Model Number".getBytes()))
                .allowMitmProtectedEncryptedRead()
                .supportReads()
                .build();

        return modelNumberStringCharacteristic;
    }

    private RxBleCharacteristic createFirmwareRevisionStringCharacteristic() {
        firmwareRevisionStringCharacteristic = new CharacteristicBuilder(FIRMWARE_REVISION_STRING_CHARACTERISTIC_UUID)
                .withInitialValue(new BaseValue("Test Firmware Revision".getBytes()))
                .allowMitmProtectedEncryptedRead()
                .supportReads()
                .build();

        return firmwareRevisionStringCharacteristic;
    }

    public RxBleServer getGattServer() {
        return gattServer;
    }

    public RxBleService getFidoService() {
        return fidoService;
    }

    public RxBleCharacteristic getFidoControlPointCharacteristic() {
        return fidoControlPointCharacteristic;
    }

    public RxBleCharacteristic getFidoStatusCharacteristic() {
        return fidoStatusCharacteristic;
    }

    public RxBleCharacteristic getFidoControlPointLengthCharacteristic() {
        return fidoControlPointLengthCharacteristic;
    }

    public RxBleCharacteristic getFidoServiceRevisionBitfieldCharacteristic() {
        return fidoServiceRevisionBitfieldCharacteristic;
    }

    public RxBleService getDeviceInformationService() {
        return deviceInformationService;
    }

    public RxBleCharacteristic getManufacturerNameStringCharacteristic() {
        return manufacturerNameStringCharacteristic;
    }

    public RxBleCharacteristic getModelNumberStringCharacteristic() {
        return modelNumberStringCharacteristic;
    }

    public RxBleCharacteristic getFirmwareRevisionStringCharacteristic() {
        return firmwareRevisionStringCharacteristic;
    }
}
