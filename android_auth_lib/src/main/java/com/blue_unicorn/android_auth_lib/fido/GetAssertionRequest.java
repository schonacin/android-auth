package com.blue_unicorn.android_auth_lib.fido;

import java.util.List;
import java.util.Map;

public interface GetAssertionRequest {

    String getRpId();

    byte[] getClientDataHash();

    List<BasePublicKeyCredentialDescriptor> getAllowList();

    Map<String, Boolean> getOptions();

}
