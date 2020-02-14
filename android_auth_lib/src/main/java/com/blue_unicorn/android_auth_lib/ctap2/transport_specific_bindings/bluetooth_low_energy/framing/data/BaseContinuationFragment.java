package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.exceptions.InvalidSequenceNumberException;

public class BaseContinuationFragment extends BaseFragment implements ContinuationFragment {

    private byte SEQ;

    public BaseContinuationFragment(byte SEQ, byte[] DATA) throws InvalidSequenceNumberException {
        super(DATA);
        setSEQ(SEQ);

        if (getSEQ() < 0)
            throw new InvalidSequenceNumberException("Invalid sequence number error: sequence number " + getSEQ() + " must be greater than zero");
    }

    public BaseContinuationFragment(byte[] rawFragment) throws InvalidSequenceNumberException {
        super(rawFragment, 1);
        setSEQ(rawFragment[0]);

        if (getSEQ() < 0)
            throw new InvalidSequenceNumberException("Invalid sequence number error: sequence number " + getSEQ() + " must be greater than zero");
    }

    @Override
    public byte getSEQ() {
        return SEQ;
    }

    @Override
    public void setSEQ(byte SEQ) {
        this.SEQ = SEQ;
    }
}
