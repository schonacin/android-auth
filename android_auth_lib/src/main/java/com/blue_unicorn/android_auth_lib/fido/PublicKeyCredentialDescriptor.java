package com.blue_unicorn.android_auth_lib.fido;

import java.util.List;

public interface PublicKeyCredentialDescriptor {

    String getType();

    byte[] getId();

    List<String> getTransports();
}
