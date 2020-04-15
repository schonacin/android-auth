package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.OtherException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BaseFrameAccumulatorTest {

    private BaseFrameAccumulator frameAccumulator;

    @Before
    public void setUp() throws OtherException {
        frameAccumulator = new BaseFrameAccumulator(32);
    }

    @Test(expected = OtherException.class)
    public void getAssembledFrameBeforeCompleteAccumulationWithErrors() throws OtherException {
        frameAccumulator.getAssembledFrame();
    }

}
