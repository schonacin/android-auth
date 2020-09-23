package com.blue_unicorn.android_auth_lib.android.authentication;

import com.blue_unicorn.android_auth_lib.android.constants.AuthenticatorAPIMethod;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;

public class BaseAuthInfo implements AuthInfo {

    private String title;
    private String rp;
    private String user;
    private @AuthenticatorAPIMethod int method;

    public BaseAuthInfo(RequestObject requestObject) {
        if (requestObject instanceof MakeCredentialRequest) {
            MakeCredentialRequest makeCredentialRequest = (MakeCredentialRequest) requestObject;
            this.title = "Registration Needs Approval!";
            this.rp = makeCredentialRequest.getRp().getId();
            this.user = makeCredentialRequest.getUser().getName();
            this.method = AuthenticatorAPIMethod.MAKE_CREDENTIAL;
        } else if (requestObject instanceof GetAssertionRequest) {
            GetAssertionRequest getAssertionRequest = (GetAssertionRequest) requestObject;
            this.title = "Authentication Needs Approval!";
            this.rp = getAssertionRequest.getRpId();
            this.user = null;
            this.method = AuthenticatorAPIMethod.GET_ASSERTION;
        } else {
            this.title = null;
            this.rp = null;
            this.user = null;
            this.method = 0;
        }
    }

    public BaseAuthInfo(String title, String rp, String user) {
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

    public int getMethod() {
        return method;
    }
}
