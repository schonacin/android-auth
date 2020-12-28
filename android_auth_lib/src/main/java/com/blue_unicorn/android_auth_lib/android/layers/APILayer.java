package com.blue_unicorn.android_auth_lib.android.layers;

/**
 * Handles the processes of the whole internal Authenticator API operations.
 */
public interface APILayer {

    void buildNewRequestChain(byte[] input);

    void updateAfterUserInteraction(boolean isApproved);

}
