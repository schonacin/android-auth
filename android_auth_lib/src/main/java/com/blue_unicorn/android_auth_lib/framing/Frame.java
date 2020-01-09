package com.blue_unicorn.android_auth_lib.framing;

// TODO: outsource construction of frame (addFragment & check & mtu) to frame builder/buffer class?
public class Frame {
    byte CMD_STAT;
    byte HLEN;
    byte LLEN;
    byte[] DATA;
    boolean[] check;
    int mtu;

    public Frame(int mtu) {
        this.mtu = mtu;
    }

    // fills CMD_STAT, HLEN, LLEN and DATA of the frame with the content of the supplied fragment
    // assumptions:
    // - except for the last fragment, all available space (= mtu) for data is used
    // - the MTU is at least 3 (to be able to transmit an initialization fragment)
    // TODO: use method overloading instead?
    public void addFragment(Fragment fragment) {

        if(fragment instanceof ContinuationFragment) {

            // maximum of wraparounds possible is 0xff (256)
            for(int wraparound = 0; wraparound < 0xff; wraparound++) {

                // at the lowest number of wraparounds where no fragment was already added
                // (as null bytes are valid data, they cannot be used as an indicator for where no
                // data of a continuation fragment was yet copied to, so we keep track of that
                // using the check array during frame construction)
                if(!check[wraparound]) {
                    check[wraparound] = true;

                    // offset introduced due to initialization fragment data
                    int initializationFragmentDataOffset = mtu - 3;

                    // offset introduced due to sequence number of current continuation fragment
                    int continuationFragmentDataOffset = (mtu - 1) * ((ContinuationFragment) fragment).SEQ;

                    // offset introduced due to wraparounds of sequence number
                    int wraparoundOffset = 0x80 * wraparound;

                    // summed offset, indicating position where data of current continuation
                    // fragment needs to be copied to
                    int offset = wraparoundOffset + continuationFragmentDataOffset + initializationFragmentDataOffset;

                    // copying data from current continuation fragment to frame data byte array
                    System.arraycopy(((ContinuationFragment) fragment).DATA, 0, this.DATA, offset, ((ContinuationFragment) fragment).DATA.length);
                    break;
                }
            }
        }

        else if(fragment instanceof InitializationFragment) {
            this.CMD_STAT = ((InitializationFragment) fragment).CMD;
            this.HLEN = ((InitializationFragment) fragment).HLEN;
            this.LLEN = ((InitializationFragment) fragment).LLEN;
            this.DATA = new byte[this.HLEN << 8 + this.LLEN];
            System.arraycopy(((InitializationFragment) fragment).DATA, 0, this.DATA, 0, ((InitializationFragment) fragment).DATA.length);
            check = new boolean[0xff];
        }
    }
}
