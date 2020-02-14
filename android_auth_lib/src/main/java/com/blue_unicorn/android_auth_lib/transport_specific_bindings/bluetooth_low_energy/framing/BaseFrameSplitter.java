package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidSequenceNumberException;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.BaseContinuationFragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.BaseInitializationFragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.ContinuationFragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Frame;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.InitializationFragment;

import java.util.ArrayList;
import java.util.List;

public class BaseFrameSplitter implements FrameSplitter {

    private int maxLen;

    BaseFrameSplitter(int maxLen) {
        setMaxLen(maxLen);
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
        int initializationFragmentDataSize = getMaxLen() - 3;
        byte[] initializationFragmentData = new byte[initializationFragmentDataSize];

        System.arraycopy(frame.getDATA(), 0, initializationFragmentData, 0, initializationFragmentDataSize);
        return new BaseInitializationFragment(frame.getCMDSTAT(), frame.getHLEN(), frame.getLLEN(), initializationFragmentData);
    }

    private List<ContinuationFragment> extractContinuationFragments(Frame frame) throws InvalidSequenceNumberException {
        int initializationFragmentDataSize = getMaxLen() - 3;
        int continuationFragmentDataSize = getMaxLen() - 1;
        List<ContinuationFragment> extractedContinuationFragments = new ArrayList<>(frame.getHLEN() << 8 + frame.getLLEN());
        byte[] continuationFragmentData = new byte[continuationFragmentDataSize];

        for (int i = initializationFragmentDataSize; i < frame.getDATA().length; i += continuationFragmentDataSize) {
            if (i < frame.getDATA().length)
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, continuationFragmentDataSize);
            else
                System.arraycopy(frame.getDATA(), i, continuationFragmentData, 0, frame.getDATA().length - i);
            extractedContinuationFragments.add(new BaseContinuationFragment((byte) (i / continuationFragmentDataSize), continuationFragmentData));
        }
        return extractedContinuationFragments;
    }

    private int getMaxLen() {
        return maxLen;
    }

    private void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

}
