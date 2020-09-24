package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidSequenceNumberException;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BaseContinuationFragment extends BaseFragment implements ContinuationFragment {

    private byte SEQ;

    public BaseContinuationFragment(byte SEQ, byte[] DATA) throws InvalidSequenceNumberException {
        this(SEQ, DATA, 0);
    }

    public BaseContinuationFragment(byte[] rawFragment) throws InvalidSequenceNumberException {
        this(rawFragment[0], rawFragment, 1);
    }

    private BaseContinuationFragment(byte SEQ, byte[] DATA, int offset) throws InvalidSequenceNumberException {
        super(DATA, offset);
        setSEQ(SEQ);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseContinuationFragment that = (BaseContinuationFragment) o;
        return getSEQ() == that.getSEQ() &&
                Arrays.equals(getDATA(), that.getDATA());
    }

    @NotNull
    @Override
    public String toString() {
        return "BaseContinuationFragment{" +
                "SEQ=" + SEQ +
                ", DATA=" + Arrays.toString(getDATA()) +
                '}';
    }
}
