package com.blue_unicorn.android_auth_lib.fido.request.properties;

/**
 * Represents requests that can be excluded
 */
public interface Excludable {

    void setExcluded(boolean excluded);

    boolean isExcluded();

}
