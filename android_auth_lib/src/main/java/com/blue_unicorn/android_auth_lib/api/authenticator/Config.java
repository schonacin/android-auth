package com.blue_unicorn.android_auth_lib.api.authenticator;

/**
 * Represents the Configuration of the Authenticator.
 * In the options, clientPin is omitted as it is not supported.
 */
final class Config {

    static String[] versions = new String[]{"FIDO_2_0"};

    static byte[] aaguid = new byte[16];

    static int maxMsgSize = 1024;

    static boolean plat = false;
    static boolean rk = true;
    static boolean up = true;
    static boolean uv = true;

}
