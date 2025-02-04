package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.ContinuationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.InitializationFragment;

import timber.log.Timber;

/*
 * Assumptions made:
 * - if there is sequence number wraparound, continuation fragments with same sequence number are in correct order (else there would not be enough information to restore the frame)
 * - maxLen is at least 3 (to be able to transmit an initialization fragment)
 * - first transmitted fragment is always the initialization fragment
 * - except for the last fragment, all available space (= maxLen) for data is used
 * - maxLen does not change
 * (last 3 assumptions are not strictly implicated by the specification, support for them could be added in the future)
 */
class BaseFrameAccumulator implements FrameAccumulator {

    private int initializationFragmentDataSize;
    private int continuationFragmentDataSize;
    private int accumulatedDataSize;
    private int totalDataSize;
    private int[] sequenceNumberCount;
    private boolean initializationFragmentAccumulated;

    private Frame frame;

    BaseFrameAccumulator(int maxLen) throws OtherException {
        Timber.d("Initializing BaseFrameAccumulator with maxLength %d", maxLen);

        if (maxLen < 3) {
            throw new OtherException("Defragmentation error: maxLen (" + maxLen + ") is smaller than minimum required maxLen (3)");
        }

        setSequenceNumberCount(new int[0x80]);
        setInitializationFragmentAccumulated(false);
        setAccumulatedDataSize(0);
        setTotalDataSize(0);
        setFragmentDataSizes(maxLen);
    }

    BaseFrameAccumulator(int maxLen, Fragment fragment) throws InvalidCommandException, InvalidLengthException, OtherException {
        this(maxLen);
        addFragment(fragment);
    }

    private void setFragmentDataSizes(int maxLen) {
        Timber.d("Set Fragment data sizes");
        setInitializationFragmentDataSize(maxLen - 3);
        Timber.d("\tSet initialization fragment data size to %d", getInitializationFragmentDataSize());
        setContinuationFragmentDataSize(maxLen - 1);
        Timber.d("\tSet continuation fragment data size to %d", getContinuationFragmentDataSize());
    }

    @Override
    public boolean isComplete() {
        boolean initializationFragmentComplete = initializationFragmentComplete();
        boolean continuationFragmentsComplete = continuationFragmentsComplete();
        boolean dataComplete = dataComplete();
        Timber.d("Check whether frame is complete based on 3 criteria:");
        Timber.d("\tinitializationFragmentComplete = %s", initializationFragmentComplete);
        Timber.d("\tcontinuationFragmentsComplete = %s", continuationFragmentsComplete);
        Timber.d("\tdataComplete = %s", dataComplete);
        Timber.d("\treturn %s", (initializationFragmentComplete && continuationFragmentsComplete && dataComplete));
        return initializationFragmentComplete && continuationFragmentsComplete && dataComplete;
    }

    private boolean initializationFragmentComplete() {
        return isInitializationFragmentAccumulated();
    }

    private boolean continuationFragmentsComplete() {
        Timber.d("Check whether continuation fragments complete");
        int continuationFragmentsCount = 0;
        for (int sequenceNumberCount : getSequenceNumberCount())
            continuationFragmentsCount += sequenceNumberCount;

        if (continuationFragmentsCount == 0) {
            Timber.d("\tNo continuous fragments exist, return totalDataSize <= initializationFragmentDataSize (%d<%d), return %s",
                    totalDataSize,
                    initializationFragmentDataSize,
                    (totalDataSize <= initializationFragmentDataSize));
            return totalDataSize <= initializationFragmentDataSize;
        }

        int frameDataSize = getAssembledLength(getFrame().getHLEN(), getFrame().getLLEN());
        int totalContinuationFragmentsDataSize = (frameDataSize - getInitializationFragmentDataSize());
        int expectedNumberOfContinuationFragments = 0;
        if (totalContinuationFragmentsDataSize != 0)
            expectedNumberOfContinuationFragments = totalContinuationFragmentsDataSize / getContinuationFragmentDataSize();
        if (totalContinuationFragmentsDataSize % getContinuationFragmentDataSize() != 0)
            expectedNumberOfContinuationFragments++;

        return continuationFragmentsCount == expectedNumberOfContinuationFragments;
    }

    private boolean dataComplete() {
        return getAccumulatedDataSize() == getTotalDataSize();
    }

    @Override
    public void addFragment(Fragment fragment) throws InvalidCommandException, InvalidLengthException, OtherException {
        Timber.d("Request to add fragment to frame");
        if (!isComplete()) {
            if (fragment instanceof ContinuationFragment)
                addContinuationFragment(((ContinuationFragment) fragment));
            else if (fragment instanceof InitializationFragment)
                addInitializationFragment(((InitializationFragment) fragment));
        }
    }

    private void addInitializationFragment(InitializationFragment fragment) throws InvalidCommandException, InvalidLengthException, OtherException {
        if (isInitializationFragmentAccumulated()) {
            throw new OtherException("Defragmentation error: received multiple initialization fragments for same frame");
        }

        Timber.d("Add initialization fragment");
        setInitializationFragmentAccumulated(true);
        setTotalDataSize(getAssembledLength(fragment.getHLEN(), fragment.getLLEN()));
        Timber.d("\tset total data size to %d", getTotalDataSize());
        setAccumulatedDataSize(getAccumulatedDataSize() + fragment.getDATA().length);
        Timber.d("\tset accumulated data size to %d", getAccumulatedDataSize());

        // impossible to reach as we assume the first fragment to be an initialization fragment
        // add this again once that assumption is not valid anymore
        //if (getAccumulatedDataSize() > getTotalDataSize())
        //    throw new InvalidLengthException("Invalid length error: accumulated data length " + getAccumulatedDataSize() + " is greater than length declared in HLEN and LLEN " + getTotalDataSize());

        byte[] dataArray = new byte[getTotalDataSize()];
        System.arraycopy(fragment.getDATA(), 0, dataArray, 0, fragment.getDATA().length);
        Timber.d("\tset frame  with command %s, HLEN %s, LLEN %s, dataArray of length %d",
                fragment.getCMD(),
                fragment.getHLEN(),
                fragment.getLLEN(),
                dataArray.length);
        setFrame(new BaseFrame(fragment.getCMD(), fragment.getHLEN(), fragment.getLLEN(), dataArray));
    }

    private void addContinuationFragment(ContinuationFragment fragment) throws InvalidLengthException {
        Timber.d("Add initialization fragment");
        setAccumulatedDataSize(getAccumulatedDataSize() + fragment.getDATA().length);

        if (isInitializationFragmentAccumulated() && getAccumulatedDataSize() > getTotalDataSize()) {
            throw new InvalidLengthException("Invalid length error: accumulated data length " + getAccumulatedDataSize() + " is greater than length declared in HLEN and LLEN " + getTotalDataSize());
        }

        int initializationFragmentDataOffset = getInitializationFragmentDataSize();
        int continuationFragmentDataOffset = getContinuationFragmentDataSize() * fragment.getSEQ();
        int wraparoundOffset = 0x80 * getContinuationFragmentDataSize() * getSequenceNumberCount()[fragment.getSEQ()];
        int totalOffset = wraparoundOffset + continuationFragmentDataOffset + initializationFragmentDataOffset;
        System.arraycopy(fragment.getDATA(), 0, getFrame().getDATA(), totalOffset, fragment.getDATA().length);
        getSequenceNumberCount()[fragment.getSEQ()]++;
    }

    private int getAssembledLength(byte HLEN, byte LLEN) {
        return ((HLEN & 0xff) << 8) + (LLEN & 0xff);
    }

    @Override
    public Frame getAssembledFrame() throws OtherException {
        if (!isComplete())
            throw new OtherException("Defragmentation error: cannot return assembled frame because it is still incomplete");
        return getFrame();
    }


    private Frame getFrame() {
        return frame;
    }

    private void setFrame(Frame frame) {
        this.frame = frame;
    }

    private int[] getSequenceNumberCount() {
        return sequenceNumberCount;
    }

    private void setSequenceNumberCount(int[] sequenceNumberCount) {
        this.sequenceNumberCount = sequenceNumberCount;
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

    private int getAccumulatedDataSize() {
        return accumulatedDataSize;
    }

    private void setAccumulatedDataSize(int accumulatedDataSize) {
        this.accumulatedDataSize = accumulatedDataSize;
    }

    private int getTotalDataSize() {
        return totalDataSize;
    }

    private void setTotalDataSize(int totalDataSize) {
        this.totalDataSize = totalDataSize;
    }

    private boolean isInitializationFragmentAccumulated() {
        return initializationFragmentAccumulated;
    }

    private void setInitializationFragmentAccumulated(boolean initializationFragmentAccumulated) {
        this.initializationFragmentAccumulated = initializationFragmentAccumulated;
    }
}
