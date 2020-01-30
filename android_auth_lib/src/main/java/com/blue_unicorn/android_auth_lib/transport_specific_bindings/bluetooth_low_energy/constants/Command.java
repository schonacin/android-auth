package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.constants;

import java.util.Arrays;
import java.util.List;

public abstract class Command {

    public static byte PING = (byte) 0x81;
    public static byte KEEPALIVE = (byte) 0x82;
    public static byte MSG = (byte) 0x83;
    public static byte CANCEL = (byte) 0xbe;
    public static byte ERROR = (byte) 0xbf;

    public static List<Byte> commands = Arrays.asList(PING, KEEPALIVE, MSG, CANCEL, ERROR);
}
