package com.blue_unicorn.android_auth_lib.android.authentication;

/**
 * Minimal Data Wrapper for Fido Requests.
 */
public interface AuthInfo {

    String getTitle();

    String getRp();

    String getUser();

    int getMethod();
}
