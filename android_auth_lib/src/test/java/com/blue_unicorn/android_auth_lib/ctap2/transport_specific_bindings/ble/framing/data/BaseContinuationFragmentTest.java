package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

import android.util.Base64;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.BleException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidSequenceNumberException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BaseContinuationFragmentTest {

    private static final String DATA = "AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==";

    @Test(expected = InvalidSequenceNumberException.class)
    public void instantiateContinuationFragmentWithInvalidLengthWithErrors() throws BleException {
        new BaseContinuationFragment((byte) 0x81, Base64.decode(DATA, Base64.DEFAULT));
    }

    @Test
    public void instantiateContinuationFragmentWithCorrectResult() throws BleException {
        ContinuationFragment continuationFragment = new BaseContinuationFragment((byte) 0x00, Base64.decode(DATA, Base64.DEFAULT));
        Assert.assertEquals(continuationFragment.getSEQ(), (byte) 0x00);
        Assert.assertArrayEquals(continuationFragment.getDATA(), Base64.decode(DATA, Base64.DEFAULT));
    }

    @Test
    public void testToString() throws BleException {
        ContinuationFragment continuationFragment = new BaseContinuationFragment((byte) 0x00, Base64.decode(DATA, Base64.DEFAULT));
        Assert.assertNotNull(continuationFragment.toString());
    }
}
