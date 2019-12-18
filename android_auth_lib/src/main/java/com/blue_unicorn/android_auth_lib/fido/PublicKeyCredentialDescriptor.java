package com.blue_unicorn.android_auth_lib.fido;

import java.util.List;

public class PublicKeyCredentialDescriptor {
    public String type;
    public byte[] id;
    public List<String> trnasports;
}
