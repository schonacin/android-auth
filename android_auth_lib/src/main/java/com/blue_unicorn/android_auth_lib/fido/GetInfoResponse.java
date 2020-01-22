package com.blue_unicorn.android_auth_lib.fido;

import java.util.Map;

public interface GetInfoResponse extends ResponseObject {
    
    void setVersions(String[] versions);

    void setAaguid(byte[] aaguid);

    void setOptions(Map<String, Boolean> options);

    void setMaxMsgSize(int maxMsgSize);

}
