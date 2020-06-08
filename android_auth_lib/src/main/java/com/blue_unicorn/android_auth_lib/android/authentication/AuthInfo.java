package com.blue_unicorn.android_auth_lib.android.authentication;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;

public class AuthInfo {

    private String title;
    private String rp;
    private String user;

    public AuthInfo(RequestObject requestObject) {
        if (requestObject instanceof MakeCredentialRequest) {
            MakeCredentialRequest makeCredentialRequest = (MakeCredentialRequest) requestObject;
            this.title = "Registration Needs Approval!";
            this.rp = makeCredentialRequest.getRp().getId();
            this.user = makeCredentialRequest.getUser().getName();
        } else if (requestObject instanceof GetAssertionRequest) {
            GetAssertionRequest getAssertionRequest = (GetAssertionRequest) requestObject;
            this.title = "Authentication Needs Approval!";
            this.rp = getAssertionRequest.getRpId();
            this.user = null;
        } else {
            this.title = null;
            this.rp = null;
            this.user = null;
        }
    }

    public AuthInfo(String title, String rp, String user) {
        this.title = title;
        this.rp = rp;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public String getRp() {
        return rp;
    }

    public String getUser() {
        return user;
    }
}
