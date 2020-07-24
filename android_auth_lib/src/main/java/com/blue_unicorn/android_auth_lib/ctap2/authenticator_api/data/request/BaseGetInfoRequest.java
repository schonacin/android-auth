package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.ExtensionSupport;

public class BaseGetInfoRequest implements GetInfoRequest {

    private boolean approved;

    private ExtensionSupport extensionSupport;

    @Override
    public boolean isApproved() {
        return approved;
    }

    @Override
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public ExtensionSupport getExtensionSupport() {
        return extensionSupport;
    }

    public void setExtensionSupport(ExtensionSupport extensionSupport) {
        this.extensionSupport = extensionSupport;
    }
}
