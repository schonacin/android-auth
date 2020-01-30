package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidSequenceNumberException;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.BaseContinuationFragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.BaseInitializationFragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.ContinuationFragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Frame;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.InitializationFragment;

import java.util.ArrayList;
import java.util.List;

// TODO: add description with reference to ctap2 spec
// assumptions made: TODO: write test for these cases
// - continuation fragments must be in correct order (sequence numbers would not make sense then)
// - if there is sequence number wraparound, continuation fragments with same sequence number are in correct order among each other
// - except for the last fragment, all available space (= maxLen) for data is used
// - the maxLen is at least 3 (to be able to transmit an initialization fragment)
public class BaseFrameBuffer implements FrameBuffer {

    private int initializationFragmentDataSize;
    private int continuationFragmentDataSize;
    private Frame frame;
    private InitializationFragment initializationFragment;
    private List<ContinuationFragment> continuationFragments;

    public BaseFrameBuffer(int maxLen) {
        setFragmentDataSizes(maxLen);
        this.continuationFragments = new ArrayList<>();
    }

    public BaseFrameBuffer(int maxLen, Frame frame) throws InvalidCommandException, InvalidSequenceNumberException, InvalidLengthException, OtherException {
        setFragmentDataSizes(maxLen);
        this.frame = frame;
        fragment();
    }

    private void setFragmentDataSizes(int maxLen) {
        this.initializationFragmentDataSize = maxLen - 3;
        this.continuationFragmentDataSize = maxLen - 1;
    }

    private void fragment() throws InvalidCommandException, InvalidSequenceNumberException, InvalidLengthException, OtherException {
        this.initializationFragment = extractInitializationFragment();
        this.continuationFragments = extractContiniationFragments();
        if(!isComplete())
            throw new OtherException("Fragmentation error: frame could not be fragmented successfully");
    }

    private InitializationFragment extractInitializationFragment() throws InvalidCommandException, InvalidLengthException {
        byte[] initializationFragmentData = new byte[this.initializationFragmentDataSize];
        System.arraycopy(frame.getDATA(), 0, initializationFragmentData, 0, this.initializationFragmentDataSize);
        return new BaseInitializationFragment(frame.getCMDSTAT(), frame.getHLEN(), frame.getLLEN(), initializationFragmentData);
    }

    private List<ContinuationFragment> extractContiniationFragments() throws InvalidSequenceNumberException {
        List<ContinuationFragment> extractedContinuationFragments = new ArrayList<>(frame.getHLEN() << 8 + frame.getLLEN());
        byte[] continuationFragmentData = new byte[this.continuationFragmentDataSize];
        for (int i = this.initializationFragmentDataSize; i < frame.getDATA().length; i += this.continuationFragmentDataSize) {
            if (i < frame.getDATA().length)
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, this.continuationFragmentDataSize);
            else
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, frame.getDATA().length - i);
            extractedContinuationFragments.add(new BaseContinuationFragment((byte) (i / this.continuationFragmentDataSize), continuationFragmentData));
        }
        return extractedContinuationFragments;
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
        for(ContinuationFragment continuationFragment : continuationFragments)
            totalDataSize += continuationFragment.getDATA().length;
        return totalDataSize == initializationFragment.getHLEN() << 8 + initializationFragment.getLLEN();
    }


    public void addFragment(Fragment fragment) throws InvalidCommandException, InvalidLengthException, OtherException {
        if (isComplete()) {
            if (fragment instanceof ContinuationFragment)
                addContinuationFragment(((ContinuationFragment) fragment));
            else if (fragment instanceof InitializationFragment)
                addInitializationFragment(((InitializationFragment) fragment));
            else
                throw new OtherException("Defragmentation error: could not add fragment of type " + fragment.getClass().getName() + "to frame buffer");
        }
    }

    private void addInitializationFragment(InitializationFragment fragment) throws InvalidCommandException, InvalidLengthException, OtherException {
        if (this.initializationFragment != null)
            throw new OtherException("Defragmentation error: received multiple initialization fragments for same frame");

        this.initializationFragment = fragment;

        byte[] dataArray = new byte[fragment.getHLEN() << 8 + fragment.getLLEN()];
        System.arraycopy(fragment.getDATA(), 0, dataArray, 0, fragment.getDATA().length);
        this.frame = new BaseFrame(fragment.getCMD(), fragment.getHLEN(), fragment.getLLEN(), dataArray);
    }

    private void addContinuationFragment(ContinuationFragment fragment) throws InvalidLengthException {
        if(this.frame.getDATA().length + fragment.getDATA().length > this.frame.getHLEN() << 8 + this.frame.getLLEN())
            throw new InvalidLengthException("Invalid length error: frame buffer DATA length " + (this.frame.getDATA().length + fragment.getDATA().length) + " is greater than length declared in HLEN and LLEN " + (this.frame.getHLEN() << 8 + this.frame.getLLEN()));

        continuationFragments.add(fragment);

        int initializationFragmentDataOffset = this.initializationFragmentDataSize;
        int continuationFragmentDataOffset = this.continuationFragmentDataSize * fragment.getSEQ();
        int wraparoundOffset = 0x80 * getNumberOfContinuationFragmentsWithSEQ(fragment.getSEQ());
        int totalOffset = wraparoundOffset + continuationFragmentDataOffset + initializationFragmentDataOffset;
        System.arraycopy(fragment.getDATA(), 0, this.frame.getDATA(), totalOffset, fragment.getDATA().length);
    }

    private int getNumberOfContinuationFragmentsWithSEQ(int SEQ) {
        int counter = 0;
        for(ContinuationFragment continuationFragment : continuationFragments)
            if(SEQ == continuationFragment.getSEQ())
                counter++;
        return counter;
    }


    public Frame getFrame() throws OtherException {
        if (!isComplete())
            throw new OtherException("Defragmentation error: frame is still incomplete");
        return this.frame;
    }

    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>(1 + continuationFragments.size());
        fragments.add(initializationFragment);
        fragments.addAll(continuationFragments);
        return fragments;
    }
}
