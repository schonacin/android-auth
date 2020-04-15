package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.gatt;

import com.nexenio.rxandroidbleserver.service.RxBleService;

public interface FidoServiceProvider {
    RxBleService getFidoService();
}
