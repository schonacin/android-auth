package com.blue_unicorn.android_auth_lib.fido.user;

public interface Approvable {

    void setApproved(boolean approved);

    boolean isApproved();

}
