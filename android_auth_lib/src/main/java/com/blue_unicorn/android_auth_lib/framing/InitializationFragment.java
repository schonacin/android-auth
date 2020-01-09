package com.blue_unicorn.android_auth_lib.framing;

public class InitializationFragment implements Fragment {
    byte CMD;
    byte HLEN;
    byte LLEN;
    byte[] DATA;

    public void Fragment(byte CMD, byte HLEN, byte LLEN, byte[] DATA) {
        this.CMD = CMD;
        this.HLEN = HLEN;
        this.LLEN = LLEN;
        this.DATA = DATA;
    }

    /*public static Fragment fromByteArray(byte[] rawFragment) {

        return new Fragment(rawFragment[0], rawFragment[1], rawFragment[2], System.arraycopy(rawFragment, 3, ));
    }*/
}
