package com.blue_unicorn.android_auth_lib.framing;

public class ContinuationFragment extends Fragment {
    private byte SEQ;
    private byte[] DATA;

    public ContinuationFragment(byte SEQ, byte[] DATA) {
        this.SEQ = SEQ;
        this.DATA = DATA;
    }

    public ContinuationFragment(byte[] rawFragment){
        this.SEQ = rawFragment[0];
        this.DATA = new byte[rawFragment.length - 1];
        System.arraycopy(rawFragment, 1, this.DATA, 0, this.DATA.length);
    }

    public byte getSEQ() {
        return SEQ;
    }

    public byte[] getDATA() {
        return DATA;
    }
}
