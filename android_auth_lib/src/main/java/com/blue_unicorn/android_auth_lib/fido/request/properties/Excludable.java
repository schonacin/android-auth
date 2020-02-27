package com.blue_unicorn.android_auth_lib.fido.request.properties;

/**
 * Represents requests that can be excluded
 */
public interface Excludable {

    boolean isExcluded();

    void setExcluded(boolean excluded);

}
