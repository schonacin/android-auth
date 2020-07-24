package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data;

public class ExtensionSupport {

    boolean continuousAuthentication;

    public ExtensionSupport() {
        this.continuousAuthentication = true;
    }

    public String[] serializeToStringArray() {
        if(continuousAuthentication) {
            return new String[]{"continuousAuthentication"};
        } else {
            return new String[]{};
        }
    }

}
