package com.blue_unicorn.android_auth_lib.framing;

public class ContinuationFragment extends Fragment {
    byte SEQ;
    byte[] DATA;

    public void Fragment(byte SEQ, byte[] DATA) {
        this.SEQ = SEQ;
        this.DATA = DATA;
    }

    /*public static Fragment fromByteArray(byte[] rawFragment) {

    };*/
}
