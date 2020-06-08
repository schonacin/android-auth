package com.blue_unicorn.android_auth_lib.ctap2.message_encoding.data;

/**
 * Represents the raw requests that are sent to the API.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#commands">specification</a>
 */
public interface Command {

    int getValue();

    String getParameters();

}
