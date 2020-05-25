package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request;

public class BaseGetInfoRequest implements GetInfoRequest {

    private boolean approved;

    @Override
    public boolean isApproved() {
        return approved;
    }

    @Override
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
