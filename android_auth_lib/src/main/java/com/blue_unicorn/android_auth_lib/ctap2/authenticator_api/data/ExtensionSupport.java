package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data;

import com.blue_unicorn.android_auth_lib.android.constants.ExtensionValue;

public class ExtensionSupport {

    boolean continuousAuthentication;

    public ExtensionSupport() {
        this.continuousAuthentication = true;
    }

    public String[] serializeToStringArray() {
        if(continuousAuthentication) {
            return new String[]{ExtensionValue.CONTINUOUS_AUTHENTICATION};
        } else {
            return new String[]{};
        }
    }

}
