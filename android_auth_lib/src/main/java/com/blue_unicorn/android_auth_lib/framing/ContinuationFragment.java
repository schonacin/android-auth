package com.blue_unicorn.android_auth_lib.framing;

public class ContinuationFragment extends Fragment {
    private byte SEQ;
    private byte[] DATA;

    public ContinuationFragment(byte SEQ, byte[] DATA) {
        this.SEQ = SEQ;
        this.DATA = DATA;
    }

    public byte getSEQ() {
        return SEQ;
    }

    public byte[] getDATA() {
        return DATA;
    }

    // TODO: why not impement this as constructor?
    /*public static Fragment fromByteArray(byte[] rawFragment) {

    };*/
}
