package com.blue_unicorn.android_auth_lib.ctap2.data.request.properties;

/**
 * Represents requests that need to be approved/ verified/ authenticated by the user
 */
public interface Approvable {

    boolean isApproved();

    void setApproved(boolean approved);

}
