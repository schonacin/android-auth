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

    // TODO: why not impement this as constructor?
    /*public static Fragment fromByteArray(byte[] rawFragment) {

        return new Fragment(rawFragment[0], rawFragment[1], rawFragment[2], System.arraycopy(rawFragment, 3, ));
    }*/
}
