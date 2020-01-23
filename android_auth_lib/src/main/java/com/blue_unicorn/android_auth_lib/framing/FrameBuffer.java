package com.blue_unicorn.android_auth_lib.framing;

import java.util.ArrayList;
import java.util.List;

// TODO: add description with reference to ctap2 spec
// assumptions made: TODO: write test for this cases
// - first fragment is always initialization fragment
// - continuation fragments must be in correct order (sequence numbers would not make sense then)
// - if there is sequence number wraparound, continuation fragments with same sequence number are in correct order among each other
// - except for the last fragment, all available space (= mtu) for data is used
// - the MTU is at least 3 (to be able to transmit an initialization fragment)
public class FrameBuffer {

    private int initializationFragmentDataSize;
    private int continuationFragmentDataSize;
    private Frame frame;
    private InitializationFragment initializationFragment;
    private List<ContinuationFragment> continuationFragments;

    public FrameBuffer(int mtu) {
        setFragmentDataSizes(mtu);
        this.continuationFragments = new ArrayList<ContinuationFragment>();
    }

    public FrameBuffer(int mtu, Frame frame) {
        setFragmentDataSizes(mtu);
        this.frame = frame;
        byte[] initializationFragmentData = new byte[this.initializationFragmentDataSize];
        System.arraycopy(frame.getDATA(), 0, initializationFragmentData, 0, this.initializationFragmentDataSize);
        initializationFragment = new InitializationFragment(frame.getCMDSTAT(), frame.getHLEN(), frame.getLLEN(), initializationFragmentData);
        continuationFragments = new ArrayList<ContinuationFragment>(frame.getHLEN() << 8 + frame.getLLEN());
        byte[] continuationFragmentData = new byte[this.continuationFragmentDataSize];
        for (int i = this.initializationFragmentDataSize; i < frame.getDATA().length; i += this.continuationFragmentDataSize) {
            if (i < frame.getDATA().length)
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, this.continuationFragmentDataSize);
            else
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, frame.getDATA().length - i);
            continuationFragments.add(new ContinuationFragment((byte) (i / this.continuationFragmentDataSize), continuationFragmentData));
        }
        if(!isComplete())
            return; // TODO: throw error: malformed frame or fragmentation went wrong
    }

    private void setFragmentDataSizes(int mtu) {
        this.initializationFragmentDataSize = mtu - 3;
        this.continuationFragmentDataSize = mtu - 1;
    }

    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>(1 + continuationFragments.size());
        fragments.add(initializationFragment);
        fragments.addAll(continuationFragments);
        return fragments;
    }

    private boolean initializationFragmentComplete() {
        return this.initializationFragment != null;
    }

    private boolean continuationFragmentsComplete() {
        int frameDataSize = this.frame.getHLEN() << 8 + this.frame.getLLEN();
        int totalContinuationFragmentsDataSize = (frameDataSize - this.initializationFragmentDataSize);
        int expectedNumberOfContinuationFragments = totalContinuationFragmentsDataSize / this.continuationFragmentDataSize + totalContinuationFragmentsDataSize % continuationFragmentDataSize / totalContinuationFragmentsDataSize;

        return continuationFragments.size() == expectedNumberOfContinuationFragments;
    }

    private boolean dataComplete() {
        int totalDataSize = initializationFragment.getDATA().length;
        for(ContinuationFragment continuationFragment : continuationFragments)
            totalDataSize += continuationFragment.getDATA().length;
        return totalDataSize == initializationFragment.getHLEN() << 8 + initializationFragment.getLLEN();
    }

    private boolean isComplete() {
        return initializationFragmentComplete() && continuationFragmentsComplete() && dataComplete();
    }

    private int getNumberOfContinuationFragmentsWithSEQ(int SEQ) {
        int counter = 0;
        for(ContinuationFragment continuationFragment : continuationFragments)
            if(SEQ == continuationFragment.getSEQ())
                counter++;
        return counter;
    }

    private void addInitializationFragment(InitializationFragment fragment) {
        if (this.initializationFragment != null)
            return; // TODO: throw error: multiple initialization fragments for same frame

        this.initializationFragment = fragment;

        byte[] dataArray = new byte[fragment.getHLEN() << 8 + fragment.getLLEN()];
        System.arraycopy(fragment.getDATA(), 0, dataArray, 0, fragment.getDATA().length);
        this.frame = new Frame(fragment.getCMD(), fragment.getHLEN(), fragment.getLLEN(), dataArray);
    }

    public void addContinuationFragment(ContinuationFragment fragment) {
        continuationFragments.add(fragment);

        int initializationFragmentDataOffset = this.initializationFragmentDataSize;
        int continuationFragmentDataOffset = this.continuationFragmentDataSize * fragment.getSEQ();
        int wraparoundOffset = 0x80 * getNumberOfContinuationFragmentsWithSEQ(fragment.getSEQ());
        int totalOffset = wraparoundOffset + continuationFragmentDataOffset + initializationFragmentDataOffset;
        System.arraycopy(fragment.getDATA(), 0, this.frame.getDATA(), totalOffset, fragment.getDATA().length);
    }

    public void addFragment(Fragment fragment) {
        if (isComplete()) {
            if (fragment instanceof ContinuationFragment)
                addContinuationFragment(((ContinuationFragment) fragment));
            else if (fragment instanceof InitializationFragment)
                addInitializationFragment(((InitializationFragment) fragment));
            else
                // TODO: throw error: could not find fitting method for adding of *insert fragment.class here* to FrameBuffer
                assert true;
        }
    }

    public Frame getFrame() {
        // TODO: should this check be here or at caller of getFrame() (outside FrameBuffer) ?
        if (!isComplete())
            return null; // TODO: throw error: frame not complete
        return this.frame;
    }
}
