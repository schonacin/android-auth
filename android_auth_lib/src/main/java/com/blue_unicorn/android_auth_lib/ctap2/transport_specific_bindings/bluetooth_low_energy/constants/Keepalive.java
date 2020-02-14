package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.constants;

import java.util.Arrays;
import java.util.List;

public abstract class Keepalive {

    private static byte PROCESSING = (byte) 0x01;
    private static byte UP_NEEDED = (byte) 0x02;

    public static List<Byte> status = Arrays.asList(PROCESSING, UP_NEEDED);
}
