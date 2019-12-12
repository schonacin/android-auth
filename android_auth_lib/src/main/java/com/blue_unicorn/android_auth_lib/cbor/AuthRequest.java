package com.blue_unicorn.android_auth_lib.cbor;

public interface AuthRequest {

    byte getCmd();

    byte[] getData();

}
