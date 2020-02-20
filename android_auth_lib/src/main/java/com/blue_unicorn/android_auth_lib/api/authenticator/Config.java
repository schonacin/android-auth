package com.blue_unicorn.android_auth_lib.api.authenticator;

/**
 * Represents the Configuration of the Authenticator.
 * In the options, clientPin is omitted as it is not supported.
 */
public final class Config {

    public static String[] VERSIONS = new String[]{"FIDO_2_0"};

    public static byte[] AAGUID = new byte[16];

    public static int MAX_MSG_SIZE = 1024;

    public static boolean PLAT = false;
    public static boolean RK = true;
    public static boolean UP = true;
    public static boolean UV = true;

    // this authenticator supports ECDSA w/ SHA-256 as registered in the IANA COSE Algorithms registry
    public static int COSE_ALGORITHM_IDENTIFIER = -7;

}
