package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidSequenceNumberException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseContinuationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseInitializationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.ContinuationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.InitializationFragment;

import java.util.ArrayList;
import java.util.List;

/*
 * Assumptions made:
 * - maxLen is at least 3 (to be able to transmit an initialization fragment)
 * - except for the last fragment, all available space (= maxLen) for data is used
 * - maxLen does not change during transmission of fragments of a frame
 * (last assumption are not strictly implicated by the specification, support for them could be added in the future)
 */
class BaseFrameSplitter implements FrameSplitter {

    private int initializationFragmentDataSize;
    private int continuationFragmentDataSize;

    BaseFrameSplitter(int maxLen) throws OtherException {
        if (maxLen < 3)
            throw new OtherException("Fragmentation error: maxLen (" + maxLen + ") is smaller than minimum required maxLen (3)");
        setFragmentDataSizes(maxLen);
    }

    private void setFragmentDataSizes(int maxLen) {
        setInitializationFragmentDataSize(maxLen - 3);
        setContinuationFragmentDataSize(maxLen - 1);
    }

    @Override
    public List<Fragment> split(Frame frame) throws InvalidCommandException, InvalidSequenceNumberException, InvalidLengthException {
        InitializationFragment initializationFragment = extractInitializationFragment(frame);
        List<ContinuationFragment> continuationFragments = extractContinuationFragments(frame);

        List<Fragment> fragments = new ArrayList<>(1 + continuationFragments.size());
        fragments.add(initializationFragment);
        fragments.addAll(continuationFragments);

        return fragments;
    }

    private InitializationFragment extractInitializationFragment(Frame frame) throws InvalidCommandException, InvalidLengthException {
        byte[] initializationFragmentData = new byte[Math.min(frame.getDATA().length, getInitializationFragmentDataSize())];

        System.arraycopy(frame.getDATA(), 0, initializationFragmentData, 0, Math.min(frame.getDATA().length, getInitializationFragmentDataSize()));
        return new BaseInitializationFragment(frame.getCMDSTAT(), frame.getHLEN(), frame.getLLEN(), initializationFragmentData);
    }

    private List<ContinuationFragment> extractContinuationFragments(Frame frame) throws InvalidSequenceNumberException {
        List<ContinuationFragment> extractedContinuationFragments = new ArrayList<>(frame.getHLEN() << 8 + frame.getLLEN());
        byte[] continuationFragmentData = new byte[getContinuationFragmentDataSize()];

        for (int i = getInitializationFragmentDataSize(); i < frame.getDATA().length; i += getContinuationFragmentDataSize()) {
            if (i < frame.getDATA().length - getContinuationFragmentDataSize()) {
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, getContinuationFragmentDataSize());
                extractedContinuationFragments.add(new BaseContinuationFragment((byte) (i / getContinuationFragmentDataSize() % 0x80), continuationFragmentData));
            } else {
                byte[] lastContinuationFragmentData = new byte[frame.getDATA().length - i];
                System.arraycopy(frame.getDATA(), i, lastContinuationFragmentData, 0, frame.getDATA().length - i);
                extractedContinuationFragments.add(new BaseContinuationFragment((byte) (i / getContinuationFragmentDataSize() % 0x80), lastContinuationFragmentData));
            }
        }
        return extractedContinuationFragments;
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
}
