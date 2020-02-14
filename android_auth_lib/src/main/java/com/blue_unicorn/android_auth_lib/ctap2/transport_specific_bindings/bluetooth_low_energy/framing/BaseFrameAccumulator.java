package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data.ContinuationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data.Frame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data.InitializationFragment;

import java.util.ArrayList;
import java.util.List;

// TODO: add description with reference to ctap2 spec
// assumptions made: TODO: write test for these cases
// - continuation fragments must be in correct order (sequence numbers would not make sense then)
// - if there is sequence number wraparound, continuation fragments with same sequence number are in correct order among each other
// - except for the last fragment, all available space (= maxLen) for data is used
// - the maxLen is at least 3 (to be able to transmit an initialization fragment)
class BaseFrameAccumulator implements FrameAccumulator {

    private int initializationFragmentDataSize;
    private int continuationFragmentDataSize;
    private Frame frame;
    private InitializationFragment initializationFragment;
    private List<ContinuationFragment> continuationFragments;


    BaseFrameAccumulator(int maxLen) {
        setFragmentDataSizes(maxLen);
        setContinuationFragments(new ArrayList<>());
    }

    BaseFrameAccumulator(int maxLen, Fragment fragment) throws InvalidCommandException, InvalidLengthException, OtherException {
        setFragmentDataSizes(maxLen);
        setContinuationFragments(new ArrayList<>());
        addFragment(fragment);
    }

    private void setFragmentDataSizes(int maxLen) {
        setInitializationFragmentDataSize(maxLen - 3);
        setContinuationFragmentDataSize(maxLen - 1);
    }

    // TODO: safe completeness state in attribute & update every time something changes
    @Override
    public boolean isComplete() {
        return initializationFragmentComplete() && continuationFragmentsComplete() && dataComplete();
    }

    private boolean initializationFragmentComplete() {
        return getInitializationFragment() != null;
    }

    private boolean continuationFragmentsComplete() {
        int frameDataSize = getFrame().getHLEN() << 8 + getFrame().getLLEN();
        int totalContinuationFragmentsDataSize = (frameDataSize - getInitializationFragmentDataSize());
        int expectedNumberOfContinuationFragments = totalContinuationFragmentsDataSize / getContinuationFragmentDataSize() + totalContinuationFragmentsDataSize % getContinuationFragmentDataSize() / totalContinuationFragmentsDataSize;

        return getContinuationFragments().size() == expectedNumberOfContinuationFragments;
    }

    private boolean dataComplete() {
        int totalDataSize = getInitializationFragment().getDATA().length;
        for (ContinuationFragment continuationFragment : getContinuationFragments())
            totalDataSize += continuationFragment.getDATA().length;
        return totalDataSize == getInitializationFragment().getHLEN() << 8 + getInitializationFragment().getLLEN();
    }

    @Override
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
        if (getInitializationFragment() != null)
            throw new OtherException("Defragmentation error: received multiple initialization fragments for same frame");

        setInitializationFragment(fragment);

        byte[] dataArray = new byte[fragment.getHLEN() << 8 + fragment.getLLEN()];
        System.arraycopy(fragment.getDATA(), 0, dataArray, 0, fragment.getDATA().length);
        setFrame(new BaseFrame(fragment.getCMD(), fragment.getHLEN(), fragment.getLLEN(), dataArray));
    }

    private void addContinuationFragment(ContinuationFragment fragment) throws InvalidLengthException {
        if (getFrame().getDATA().length + fragment.getDATA().length > getFrame().getHLEN() << 8 + getFrame().getLLEN())
            throw new InvalidLengthException("Invalid length error: frame buffer DATA length " + (getFrame().getDATA().length + fragment.getDATA().length) + " is greater than length declared in HLEN and LLEN " + (getFrame().getHLEN() << 8 + getFrame().getLLEN()));

        getContinuationFragments().add(fragment);

        int initializationFragmentDataOffset = getInitializationFragmentDataSize();
        int continuationFragmentDataOffset = getContinuationFragmentDataSize() * fragment.getSEQ();
        int wraparoundOffset = 0x80 * getNumberOfContinuationFragmentsWithSEQ(fragment.getSEQ());
        int totalOffset = wraparoundOffset + continuationFragmentDataOffset + initializationFragmentDataOffset;
        System.arraycopy(fragment.getDATA(), 0, getFrame().getDATA(), totalOffset, fragment.getDATA().length);
    }

    // TODO: inefficient, seek faster solution
    private int getNumberOfContinuationFragmentsWithSEQ(int SEQ) {
        int counter = 0;
        for (ContinuationFragment continuationFragment : getContinuationFragments())
            if (SEQ == continuationFragment.getSEQ())
                counter++;
        return counter;
    }

    @Override
    public Frame getAssembledFrame() throws OtherException {
        if (!isComplete())
            throw new OtherException("Defragmentation error: frame is still incomplete");
        return getFrame();
    }

    private Frame getFrame() {
        return frame;
    }

    private void setFrame(Frame frame) {
        this.frame = frame;
    }

    private int getInitializationFragmentDataSize() {
        return initializationFragmentDataSize;
    }

    private void setInitializationFragmentDataSize(int initializationFragmentDataSize) {
        this.initializationFragmentDataSize = initializationFragmentDataSize;
    }

    private int getContinuationFragmentDataSize() {
        return continuationFragmentDataSize;
    }

    private void setContinuationFragmentDataSize(int continuationFragmentDataSize) {
        this.continuationFragmentDataSize = continuationFragmentDataSize;
    }

    private InitializationFragment getInitializationFragment() {
        return initializationFragment;
    }

    private void setInitializationFragment(InitializationFragment initializationFragment) {
        this.initializationFragment = initializationFragment;
    }

    private List<ContinuationFragment> getContinuationFragments() {
        return continuationFragments;
    }

    private void setContinuationFragments(List<ContinuationFragment> continuationFragments) {
        this.continuationFragments = continuationFragments;
    }
}
