package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidSequenceNumberException;
import com.nexenio.rxandroidbleserver.service.value.ValueUtil;

import timber.log.Timber;

public interface Fragment {

    byte[] getDATA();

    byte[] asBytes();

    static Fragment toFragment(byte[] bytes) throws InvalidLengthException, InvalidCommandException, InvalidSequenceNumberException {
        if ((bytes[0] & (byte) 0x80) == (byte) 0x80) {
            Timber.d("\tFragment of request is init fragment");
            return new BaseInitializationFragment(bytes);
        } else {
            ContinuationFragment continuationFragment = new BaseContinuationFragment(bytes);
            Timber.d("\tFragment is continuation fragment #%s", ValueUtil.bytesToHex(new byte[]{continuationFragment.getSEQ()}));
            return continuationFragment;
        }
    }

}
