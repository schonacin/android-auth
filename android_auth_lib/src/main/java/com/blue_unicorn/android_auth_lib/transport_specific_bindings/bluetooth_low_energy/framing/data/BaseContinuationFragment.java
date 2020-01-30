package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidSequenceNumberException;

public class BaseContinuationFragment extends BaseFragment implements ContinuationFragment {

    private byte SEQ;

    public BaseContinuationFragment(byte SEQ, byte[] DATA) throws InvalidSequenceNumberException {
        super(DATA);
        this.SEQ = SEQ;

        if(SEQ < 0)
            throw new InvalidSequenceNumberException("Invalid sequence number error: sequence number " + SEQ + " must be greater than zero");
    }

    public BaseContinuationFragment(byte[] rawFragment) throws InvalidSequenceNumberException {
        super(rawFragment, 1);
        this.SEQ = rawFragment[0];

        if(SEQ < 0)
            throw new InvalidSequenceNumberException("Invalid sequence number error: sequence number " + SEQ + " must be greater than zero");
    }

    public byte getSEQ() {
        return SEQ;
    }
}
