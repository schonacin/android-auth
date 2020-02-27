package com.blue_unicorn.android_auth_lib.api.authenticator;

/**
 * Represents the Configuration of the Authenticator.
 * In the options, clientPin is omitted as it is not supported.
 */
public final class Config {

    /**
     * List of supported versions.
     * Supported versions are:
     * "FIDO_2_0" for CTAP2 / FIDO2 / Web Authentication authenticators
     */
    public static String[] VERSIONS = new String[]{"FIDO_2_0"};

    /**
     * The claimed AAGUID.
     * 128-bit identifier indicating the type (e.g. make and model) of the authenticator
     */
    public static byte[] AAGUID = new byte[16];

    /**
     * Maximum message size supported by the authenticator
     */
    public static int MAX_MSG_SIZE = 1024;

    /**
     * Option: platform device
     * Indicates that the device is attached to the client and therefore can’t be removed and used on another client
     */
    public static boolean PLAT = false;

    /**
     * Option: resident key
     * Indicates that the device is capable of storing keys on the device itself
     */
    public static boolean RK = true;

    /**
     * Option: user presence
     * Indicates that the device is capable of testing user presence
     */
    public static boolean UP = true;

    /**
     * Option: user verification
     * Indicates that the device is capable of verifying the user within itself
     */
    public static boolean UV = true;

    /**
     * The supported COSEAlgorithmIdentifier.
     * -7 for ECDSA w/ SHA-256 as registered in the IANA COSE Algorithms registry
     */
    public static int COSE_ALGORITHM_IDENTIFIER = -7;

}
