package com.blue_unicorn.android_auth_lib.android.layers;

import androidx.annotation.Nullable;

/**
 * Handles the processes of the whole internal Authenticator API operations.
 */
public interface APILayer {

    void buildNewRequestChain(byte[] input);

    void updateAfterUserInteraction(boolean isApproved);

    void updateAfterUserInteraction(boolean isApproved, @Nullable Integer freshness);
}
