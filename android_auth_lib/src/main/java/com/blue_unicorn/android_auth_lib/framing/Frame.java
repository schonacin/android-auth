package com.blue_unicorn.android_auth_lib.framing;

// TODO: outsource fragment-by-fragment construction of frame (addFragment method, check/mtu
//  attribute) to FrameBuilder/FrameBuffer class, or even to FrameHandler?
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
    // assumptions made:
    // - except for the last fragment, all available space (= mtu) for data is used
    // - the MTU is at least 3 (to be able to transmit an initialization fragment)
    // TODO: use method overloading instead of choosing methods based on instanceof check?
    public void addFragment(Fragment fragment) {
        if(fragment instanceof ContinuationFragment)
            addContinuationFragment(((ContinuationFragment) fragment));
        else if(fragment instanceof InitializationFragment)
            addInitializationFragment(((InitializationFragment) fragment));
    }

    // TODO: replace for-loop with break statement, use while loop instead?
    public void addContinuationFragment(ContinuationFragment fragment) {
        // maximum of wraparounds possible is 0xff (256)
        for(int wraparound = 0; wraparound < 0xff; wraparound++) {

            // at the lowest number of wraparounds where no fragment was already added
            // (as null bytes are valid data, they cannot be used as an indicator for where no
            // data of a continuation fragment was yet copied to, so we keep track of that using
            // the check array during frame construction)
            if(!check[wraparound]) {
                check[wraparound] = true;

                // offset introduced due to initialization fragment data
                int initializationFragmentDataOffset = mtu - 3;

                // offset introduced due to sequence number of current continuation fragment
                int continuationFragmentDataOffset = (mtu - 1) * fragment.SEQ;

                // offset introduced due to wraparounds of sequence number (0x80 continuation fragments per wraparound)
                int wraparoundOffset = 0x80 * wraparound;

                // total offset, indicating position where data of current continuation fragment
                // needs to be copied to
                int totalOffset = wraparoundOffset + continuationFragmentDataOffset + initializationFragmentDataOffset;

                // copying data from current continuation fragment to frame data byte array
                System.arraycopy(fragment.DATA, 0, this.DATA, totalOffset, fragment.DATA.length);
                break;
            }
        }
    }

    public void addInitializationFragment(InitializationFragment fragment) {
        this.CMD_STAT = fragment.CMD;
        this.HLEN = fragment.HLEN;
        this.LLEN = fragment.LLEN;
        this.DATA = new byte[this.HLEN << 8 + this.LLEN];
        System.arraycopy(fragment.DATA, 0, this.DATA, 0, fragment.DATA.length);
        check = new boolean[0xff];
    }
}
