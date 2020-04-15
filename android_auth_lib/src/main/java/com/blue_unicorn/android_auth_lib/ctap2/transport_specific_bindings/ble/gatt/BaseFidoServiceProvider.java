package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt;

import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.ServiceBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.CharacteristicBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class BaseFidoServiceProvider implements FidoServiceProvider {

    @Override
    public RxBleService getFidoService() {
        return new ServiceBuilder(UUID.fromString("0000FFFD-0000-1000-8000-00805F9B34FB"))
                .withCharacteristic(getFidoControlPointCharacteristic())
                .withCharacteristic(getFidoStatusCharacteristic())
                .withCharacteristic(getFidoControlPointLengthCharacteristic())
                .withCharacteristic(getFidoServiceRevisionBitfieldCharacteristic())
                .isPrimaryService()
                .build();
    }

    private RxBleCharacteristic getFidoControlPointCharacteristic() {
        return new CharacteristicBuilder(UUID.fromString("F1D0FFF1-DEAA-ECEE-B42F-C9BA7ED623BB"))
                .allowMitmProtectedEncryptedWrite()
                .supportWrites()
                .build();
    }

    private RxBleCharacteristic getFidoStatusCharacteristic() {
        return new CharacteristicBuilder(UUID.fromString("F1D0FFF2-DEAA-ECEE-B42F-C9BA7ED623BB"))
                .supportNotifications()
                .build();
    }

    // TODO: set and update FidoControlPointLength depending on att_mtu
    private RxBleCharacteristic getFidoControlPointLengthCharacteristic() {
        return new CharacteristicBuilder(UUID.fromString("F1D0FFF3-DEAA-ECEE-B42F-C9BA7ED623BB"))
                .withInitialValue(new BaseValue(ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putInt(512).array()))
                .allowMitmProtectedEncryptedRead()
                .supportReads()
                .build();
    }

    private RxBleCharacteristic getFidoServiceRevisionBitfieldCharacteristic() {
        return new CharacteristicBuilder(UUID.fromString("F1D0FFF4-DEAA-ECEE-B42F-C9BA7ED623BB"))
                .withInitialValue(new BaseValue(new byte[]{0x20}))
                .allowMitmProtectedEncryptedRead()
                .allowMitmProtectedEncryptedWrite()
                .supportReads()
                .supportWrites()
                .build();
    }
}
