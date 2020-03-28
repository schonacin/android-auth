package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

import android.util.Base64;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.BleException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidLengthException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BaseFrameTest {

    private static final String DATA = "AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==";

    @Test(expected = InvalidLengthException.class)
    public void instantiateFrameWithInvalidLengthWithErrors() throws BleException {
        new BaseFrame((byte) 0x81, (byte) 0x00, (byte) 0x69, Base64.decode(DATA, Base64.DEFAULT));
    }

    @Test(expected = InvalidCommandException.class)
    public void instantiateFrameWithInvalidCommandWithErrors() throws BleException {
        new BaseFrame((byte) 0x42, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
    }

    @Test
    public void instantiateFrameWithCorrectResult() throws BleException {
        Frame frame = new BaseFrame((byte) 0x83, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        Assert.assertEquals(frame.getCMDSTAT(), (byte) 0x83);
        Assert.assertEquals(frame.getHLEN(), (byte) 0x00);
        Assert.assertEquals(frame.getLLEN(), (byte) 0x6a);
        Assert.assertArrayEquals(frame.getDATA(), Base64.decode(DATA, Base64.DEFAULT));
    }

    @Test
    public void testToString() throws BleException {
        Frame frame = new BaseFrame((byte) 0x83, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        Assert.assertNotNull(frame.toString());
    }
}
