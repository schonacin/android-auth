package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.properties;

/**
 * Represents requests that can be excluded
 */
public interface Excludable {

    boolean isExcluded();

    void setExcluded(boolean excluded);

}
