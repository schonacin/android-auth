package com.blue_unicorn.android_auth_lib.fido.request;

import com.blue_unicorn.android_auth_lib.fido.FidoObject;

public interface RequestObject extends FidoObject {

    boolean isValid();

}
