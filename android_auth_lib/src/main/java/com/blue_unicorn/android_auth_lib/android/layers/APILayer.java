package com.blue_unicorn.android_auth_lib.android.layers;

public interface APILayer {

    void buildNewRequestChain(byte[] input);

    void buildResponseChainAfterUserInteraction(boolean isApproved);

}
