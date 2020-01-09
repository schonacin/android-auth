package com.blue_unicorn.android_auth_lib.framing;

import java.util.List;

public class Frame {
    byte CMD_STAT;
    byte HLEN;
    byte LLEN;
    byte[] DATA;

    public Frame() {}

    /*public Frame(byte CMD_STAT, byte HLEN, byte LLEN, byte[] DATA) {
        this.CMD_STAT = CMD_STAT;
        this.HLEN = HLEN;
        this.LLEN = LLEN;
        this.DATA = DATA;
    }*/

    public void addFragment(Fragment fragment) {
        if(fragment instanceof ContinuationFragment) {
            assert true;
        }
        else if(fragment instanceof InitializationFragment) {
            this.CMD_STAT = ((InitializationFragment) fragment).CMD;
            this.HLEN = ((InitializationFragment) fragment).HLEN;
            this.LLEN = ((InitializationFragment) fragment).LLEN;
            this.DATA = new byte[this.HLEN << 8 + this.LLEN];
            System.arraycopy(((InitializationFragment) fragment).DATA, 0, this.DATA, 0, ((InitializationFragment) fragment).DATA.length);
        }
    }
}
