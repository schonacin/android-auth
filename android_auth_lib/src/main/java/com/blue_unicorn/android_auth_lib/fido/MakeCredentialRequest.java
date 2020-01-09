package com.blue_unicorn.android_auth_lib.fido;

import java.util.List;
import java.util.Map;

public interface MakeCredentialRequest extends RequestObject {

    byte[] getClientDataHash();

    BasePublicKeyCredentialRpEntity getRp();

    BasePublicKeyCredentialUserEntity getUser();

    Map[] getPubKeyCredParams();

    List<BasePublicKeyCredentialDescriptor> getExcludeList();

    Map<String, Boolean> getOptions();

}
