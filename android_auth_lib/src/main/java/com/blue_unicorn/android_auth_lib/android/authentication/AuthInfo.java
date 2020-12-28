package com.blue_unicorn.android_auth_lib.android.authentication;

import com.blue_unicorn.android_auth_lib.android.constants.AuthenticatorAPIMethod;

/**
 * Minimal Data Wrapper for Fido Requests.
 */
public interface AuthInfo {

    String getTitle();

    String getRp();

    String getUser();

    @AuthenticatorAPIMethod int getMethod();
}
