package com.blue_unicorn.android_auth_lib.framing;

public class InitializationFragment extends Fragment {
    private byte CMD;
    private byte HLEN;
    private byte LLEN;
    private byte[] DATA;

    public InitializationFragment(byte CMD, byte HLEN, byte LLEN, byte[] DATA) {
        this.CMD = CMD;
        this.HLEN = HLEN;
        this.LLEN = LLEN;
        this.DATA = DATA;
    }

    public InitializationFragment(byte[] rawFragment){
        this.CMD = rawFragment[0];
        this.HLEN = rawFragment[1];
        this.LLEN = rawFragment[2];
        this.DATA = new byte[rawFragment.length - 3];
        System.arraycopy(rawFragment, 3, this.DATA, 0, this.DATA.length);
    }

    public byte getCMD() {
        return CMD;
    }

    public byte getHLEN() {
        return HLEN;
    }

    public byte getLLEN() {
        return LLEN;
    }

    public byte[] getDATA() {
        return DATA;
    }
}
