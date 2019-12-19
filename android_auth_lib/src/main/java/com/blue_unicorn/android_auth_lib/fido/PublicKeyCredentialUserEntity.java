package com.blue_unicorn.android_auth_lib.fido;

public interface PublicKeyCredentialUserEntity {

    byte[] getId();

    String getName();

    String getDisplayName();
}
