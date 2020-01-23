package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

import java.util.ArrayList;
import java.util.List;

// TODO: add description with reference to ctap2 spec
// assumptions made: TODO: write test for this cases
// - continuation fragments must be in correct order (sequence numbers would not make sense then)
// - if there is sequence number wraparound, continuation fragments with same sequence number are in correct order among each other
// - except for the last fragment, all available space (= maxLen) for data is used
// - the maxLen is at least 3 (to be able to transmit an initialization fragment)
public class BaseFrameBuffer implements FrameBuffer {

    private int initializationFragmentDataSize;
    private int continuationFragmentDataSize;
    private BaseFrame frame;
    private BaseInitializationFragment initializationFragment;
    private List<BaseContinuationFragment> continuationFragments;


    public BaseFrameBuffer(int maxLen) {
        setFragmentDataSizes(maxLen);
        this.continuationFragments = new ArrayList<>();
    }

    public BaseFrameBuffer(int maxLen, BaseFrame frame) {
        setFragmentDataSizes(maxLen);
        this.frame = frame;
        byte[] initializationFragmentData = new byte[this.initializationFragmentDataSize];
        System.arraycopy(frame.getDATA(), 0, initializationFragmentData, 0, this.initializationFragmentDataSize);
        initializationFragment = new BaseInitializationFragment(frame.getCMDSTAT(), frame.getHLEN(), frame.getLLEN(), initializationFragmentData);
        continuationFragments = new ArrayList<>(frame.getHLEN() << 8 + frame.getLLEN());
        byte[] continuationFragmentData = new byte[this.continuationFragmentDataSize];
        for (int i = this.initializationFragmentDataSize; i < frame.getDATA().length; i += this.continuationFragmentDataSize) {
            if (i < frame.getDATA().length)
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, this.continuationFragmentDataSize);
            else
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, frame.getDATA().length - i);
            continuationFragments.add(new BaseContinuationFragment((byte) (i / this.continuationFragmentDataSize), continuationFragmentData));
        }
        if(!isComplete())
            return; // TODO: throw error: malformed frame (or fragmentation went wrong)
    }

    private void setFragmentDataSizes(int maxLen) {
        this.initializationFragmentDataSize = maxLen - 3;
        this.continuationFragmentDataSize = maxLen - 1;
    }


    public boolean isComplete() {
        return initializationFragmentComplete() && continuationFragmentsComplete() && dataComplete();
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
        for(BaseContinuationFragment continuationFragment : continuationFragments)
            totalDataSize += continuationFragment.getDATA().length;
        return totalDataSize == initializationFragment.getHLEN() << 8 + initializationFragment.getLLEN();
    }


    public void addFragment(BaseFragment fragment) {
        if (isComplete()) {
            if (fragment instanceof BaseContinuationFragment)
                addContinuationFragment(((BaseContinuationFragment) fragment));
            else if (fragment instanceof BaseInitializationFragment)
                addInitializationFragment(((BaseInitializationFragment) fragment));
            else
                // TODO: throw error: could not find fitting method for adding of *insert fragment.class here* to BaseFrameBuffer
                assert true;
        }
    }

    private void addInitializationFragment(BaseInitializationFragment fragment) {
        if (this.initializationFragment != null)
            return; // TODO: throw error: multiple initialization fragments for same frame

        this.initializationFragment = fragment;

        byte[] dataArray = new byte[fragment.getHLEN() << 8 + fragment.getLLEN()];
        System.arraycopy(fragment.getDATA(), 0, dataArray, 0, fragment.getDATA().length);
        this.frame = new BaseFrame(fragment.getCMD(), fragment.getHLEN(), fragment.getLLEN(), dataArray);
    }

     private void addContinuationFragment(BaseContinuationFragment fragment) {
        continuationFragments.add(fragment);

        int initializationFragmentDataOffset = this.initializationFragmentDataSize;
        int continuationFragmentDataOffset = this.continuationFragmentDataSize * fragment.getSEQ();
        int wraparoundOffset = 0x80 * getNumberOfContinuationFragmentsWithSEQ(fragment.getSEQ());
        int totalOffset = wraparoundOffset + continuationFragmentDataOffset + initializationFragmentDataOffset;
        System.arraycopy(fragment.getDATA(), 0, this.frame.getDATA(), totalOffset, fragment.getDATA().length);
    }

    private int getNumberOfContinuationFragmentsWithSEQ(int SEQ) {
        int counter = 0;
        for(BaseContinuationFragment continuationFragment : continuationFragments)
            if(SEQ == continuationFragment.getSEQ())
                counter++;
        return counter;
    }


    public BaseFrame getFrame() {
        if (!isComplete())
            return null; // TODO: throw error: frame incomplete
        return this.frame;
    }

    public List<BaseFragment> getFragments() {
        List<BaseFragment> fragments = new ArrayList<>(1 + continuationFragments.size());
        fragments.add(initializationFragment);
        fragments.addAll(continuationFragments);
        return fragments;
    }
}
