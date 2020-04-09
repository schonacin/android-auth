package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt;

import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.ServiceBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.CharacteristicBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// TODO: force app devs who use this lib to set authenticator-specific parameters (like manufacturer name, model number etc.)
//  (e.g. by providing an instance of a config class defined by the lib, builder pattern?)
public abstract class BaseDeviceInformationServiceProvider implements DeviceInformationServiceProvider {

    @Override
    public RxBleService getDeviceInformationService() {
        return new ServiceBuilder(UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB"))
                .withCharacteristic(getManufacturerNameStringCharacteristic())
                .withCharacteristic(getModelNumberStringCharacteristic())
                .withCharacteristic(getFirmwareRevisionStringCharacteristic())
                .isPrimaryService()
                .build();
    }

    private RxBleCharacteristic getManufacturerNameStringCharacteristic() {
        return new CharacteristicBuilder(UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB"))
                .withInitialValue(new BaseValue("Test Manufacturer String".getBytes()))
                .allowMitmProtectedEncryptedRead()
                .supportReads()
                .build();
    }

    private RxBleCharacteristic getModelNumberStringCharacteristic() {
        return new CharacteristicBuilder(UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB"))
                .withInitialValue(new BaseValue("Test Model Number".getBytes()))
                .allowMitmProtectedEncryptedRead()
                .supportReads()
                .build();
    }

    private RxBleCharacteristic getFirmwareRevisionStringCharacteristic() {
        return new CharacteristicBuilder(UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB"))
                .withInitialValue(new BaseValue("Test Firmware Revision".getBytes(StandardCharsets.UTF_8)))
                .allowMitmProtectedEncryptedRead()
                .supportReads()
                .build();
    }
}
