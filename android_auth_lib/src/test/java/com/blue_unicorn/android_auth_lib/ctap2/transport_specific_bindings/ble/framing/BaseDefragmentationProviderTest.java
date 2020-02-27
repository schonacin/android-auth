package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.BleException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidSequenceNumberException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseContinuationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseInitializationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@RunWith(RobolectricTestRunner.class)
public class BaseDefragmentationProviderTest {

    private BaseDefragmentationProvider defragmentationProvider;

    // TODO: put this into tests
    @Before
    public void setUp() throws InvalidCommandException, InvalidLengthException, InvalidSequenceNumberException {
        defragmentationProvider = new BaseDefragmentationProvider();


        // non-last fragment not filled completely / mtu changes during transmission
        // TODO: can this be implemented in a way that it works?
        // TODO: can a changing maxLen work?

        // test what happens if first fragment is continuation fragment
        // TODO: can we make it work?

        // test if defragmentation of fragmented frame (created by FragmentationProvider) results in original frame (assuming same maxLen)
    }

    @Test
    public void transformsSingleFragmentWithNoErrors() throws BleException {
        // no fragmentation, mtu greater than header + data
        int TEST_SINGLE_FRAGMENT_MAXLEN = 1024;
        Fragment TEST_SINGLE_FRAGMENT_INIT_FRAG = new BaseInitializationFragment((byte) 0x81, (byte) 0x00, (byte) 0x6a, new byte[]{(byte) 0x02, (byte) 0xa3, (byte) 0x01, (byte) 0x6b, (byte) 0x77, (byte) 0x65, (byte) 0x62, (byte) 0x61, (byte) 0x75, (byte) 0x74, (byte) 0x68, (byte) 0x6e, (byte) 0x2e, (byte) 0x69, (byte) 0x6f, (byte) 0x02, (byte) 0x58, (byte) 0x20, (byte) 0x28, (byte) 0x7a, (byte) 0x01, (byte) 0x7f, (byte) 0x92, (byte) 0x90, (byte) 0x15, (byte) 0x6f, (byte) 0x65, (byte) 0x61, (byte) 0x8f, (byte) 0x13, (byte) 0x42, (byte) 0xeb, (byte) 0x96, (byte) 0x53, (byte) 0x2e, (byte) 0x57, (byte) 0x6a, (byte) 0x78, (byte) 0x86, (byte) 0x0a, (byte) 0x25, (byte) 0xcc, (byte) 0x3b, (byte) 0x09, (byte) 0xcb, (byte) 0xb0, (byte) 0x95, (byte) 0x93, (byte) 0xf7, (byte) 0xb9, (byte) 0x03, (byte) 0x81, (byte) 0xa2, (byte) 0x62, (byte) 0x69, (byte) 0x64, (byte) 0x58, (byte) 0x20, (byte) 0x7c, (byte) 0xe0, (byte) 0xbe, (byte) 0xf2, (byte) 0x27, (byte) 0x8e, (byte) 0x10, (byte) 0xc4, (byte) 0x74, (byte) 0x75, (byte) 0xfd, (byte) 0xde, (byte) 0x1b, (byte) 0x4d, (byte) 0xc7, (byte) 0x20, (byte) 0x4d, (byte) 0xf9, (byte) 0x73, (byte) 0xd5, (byte) 0xa8, (byte) 0xa8, (byte) 0xae, (byte) 0xd1, (byte) 0x4f, (byte) 0x32, (byte) 0x27, (byte) 0x44, (byte) 0x24, (byte) 0xa8, (byte) 0xe5, (byte) 0x42, (byte) 0x64, (byte) 0x74, (byte) 0x79, (byte) 0x70, (byte) 0x65, (byte) 0x6a, (byte) 0x70, (byte) 0x75, (byte) 0x62, (byte) 0x6c, (byte) 0x69, (byte) 0x63, (byte) 0x2d, (byte) 0x6b, (byte) 0x65, (byte) 0x79});

        defragmentationProvider.defragment(Flowable.just(TEST_SINGLE_FRAGMENT_INIT_FRAG), TEST_SINGLE_FRAGMENT_MAXLEN).test().assertNoErrors();
    }

    /*@Test
    public void transformsSingleFragmentWithCorrectResult() {

    }*/


    @Test
    public void transformsMultipleFragmentsOutOffOrderWithoutWraparoundWithNoErrors() throws BleException {
        // fragmentation with multiple fragments without wraparound, out of order
        int TEST_MULTIPLE_FRAGMENTS_MAXLEN = 32;
        Fragment TEST_MULTIPLE_FRAGMENTS_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x6a, new byte[]{(byte) 0x02, (byte) 0xa3, (byte) 0x01, (byte) 0x6b, (byte) 0x77, (byte) 0x65, (byte) 0x62, (byte) 0x61, (byte) 0x75, (byte) 0x74, (byte) 0x68, (byte) 0x6e, (byte) 0x2e, (byte) 0x69, (byte) 0x6f, (byte) 0x02, (byte) 0x58, (byte) 0x20, (byte) 0x28, (byte) 0x7a, (byte) 0x01, (byte) 0x7f, (byte) 0x92, (byte) 0x90, (byte) 0x15, (byte) 0x6f, (byte) 0x65, (byte) 0x61, (byte) 0x8f});
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_0 = new BaseContinuationFragment((byte) 0, new byte[]{(byte) 0x13, (byte) 0x42, (byte) 0xeb, (byte) 0x96, (byte) 0x53, (byte) 0x2e, (byte) 0x57, (byte) 0x6a, (byte) 0x78, (byte) 0x86, (byte) 0x0a, (byte) 0x25, (byte) 0xcc, (byte) 0x3b, (byte) 0x09, (byte) 0xcb, (byte) 0xb0, (byte) 0x95, (byte) 0x93, (byte) 0xf7, (byte) 0xb9, (byte) 0x03, (byte) 0x81, (byte) 0xa2, (byte) 0x62, (byte) 0x69, (byte) 0x64, (byte) 0x58, (byte) 0x20, (byte) 0x7c, (byte) 0xe0});
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_1 = new BaseContinuationFragment((byte) 1, new byte[]{(byte) 0xbe, (byte) 0xf2, (byte) 0x27, (byte) 0x8e, (byte) 0x10, (byte) 0xc4, (byte) 0x74, (byte) 0x75, (byte) 0xfd, (byte) 0xde, (byte) 0x1b, (byte) 0x4d, (byte) 0xc7, (byte) 0x20, (byte) 0x4d, (byte) 0xf9, (byte) 0x73, (byte) 0xd5, (byte) 0xa8, (byte) 0xa8, (byte) 0xae, (byte) 0xd1, (byte) 0x4f, (byte) 0x32, (byte) 0x27, (byte) 0x44, (byte) 0x24, (byte) 0xa8, (byte) 0xe5, (byte) 0x42, (byte) 0x64});
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_2 = new BaseContinuationFragment((byte) 2, new byte[]{(byte) 0x74, (byte) 0x79, (byte) 0x70, (byte) 0x65, (byte) 0x6a, (byte) 0x70, (byte) 0x75, (byte) 0x62, (byte) 0x6c, (byte) 0x69, (byte) 0x63, (byte) 0x2d, (byte) 0x6b, (byte) 0x65, (byte) 0x79});

        defragmentationProvider.defragment(Flowable.just(TEST_MULTIPLE_FRAGMENTS_INIT_FRAG, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_1, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_0, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_2), TEST_MULTIPLE_FRAGMENTS_MAXLEN).test().assertNoErrors();
    }

    /*@Test
    public void transformsMultipleFragmentsWithoutWraparoundWithCorrectResult() {

    }*/


    @Test
    public void transformsMultipleFragmentsWithSingleWraparoundWithNoErrors() throws BleException {
        int TEST_ONE_WRAPAROUND_MAXLEN = 24;
        Fragment TEST_ONE_WRAPAROUND_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x0c, (byte) 0x66, new byte[]{(byte) 0x02, (byte) 0xa3, (byte) 0x01, (byte) 0x6b, (byte) 0x77, (byte) 0x65, (byte) 0x62, (byte) 0x61, (byte) 0x75, (byte) 0x74, (byte) 0x68, (byte) 0x6e, (byte) 0x2e, (byte) 0x69, (byte) 0x6f, (byte) 0x02, (byte) 0x58, (byte) 0x20, (byte) 0x28, (byte) 0x7a, (byte) 0x01});
        byte[] TEST_ONE_WRAPAROUND_CONT_FRAG_DATA = new byte[]{(byte) 0x7f, (byte) 0x92, (byte) 0x90, (byte) 0x15, (byte) 0x6f, (byte) 0x65, (byte) 0x61, (byte) 0x8f, (byte) 0x13, (byte) 0x42, (byte) 0xeb, (byte) 0x96, (byte) 0x53, (byte) 0x2e, (byte) 0x57, (byte) 0x6a, (byte) 0x78, (byte) 0x86, (byte) 0x0a, (byte) 0x25, (byte) 0xcc, (byte) 0x3b, (byte) 0x09};
        List<Fragment> TEST_ONE_WRAPAROUND_FRAGMENTS = new ArrayList<>();
        TEST_ONE_WRAPAROUND_FRAGMENTS.add(TEST_ONE_WRAPAROUND_INIT_FRAG);
        // same continuation fragment is used multiple times
        for(int i = 0; i < 0x89; i++)
            TEST_ONE_WRAPAROUND_FRAGMENTS.add(new BaseContinuationFragment((byte) (i % 0x80), TEST_ONE_WRAPAROUND_CONT_FRAG_DATA));

        defragmentationProvider.defragment(Flowable.fromIterable(TEST_ONE_WRAPAROUND_FRAGMENTS), TEST_ONE_WRAPAROUND_MAXLEN).test().assertNoErrors();
    }

    /*@Test
    public void transformsMultipleFragmentsWithSingleWraparoundWithCorrectResult() {

    }*/


    @Test
    public void transformsMultipleFragmentsWithMultipleWraparoundWithNoErrors() throws BleException {
        int TEST_MULTIPLE_WRAPAROUNDS_MAXLEN = 64;
        Fragment TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x3e, (byte) 0xfe, new byte[]{(byte) 0x02, (byte) 0xa3, (byte) 0x01, (byte) 0x6b, (byte) 0x77, (byte) 0x65, (byte) 0x62, (byte) 0x61, (byte) 0x75, (byte) 0x74, (byte) 0x68, (byte) 0x6e, (byte) 0x2e, (byte) 0x69, (byte) 0x6f, (byte) 0x02, (byte) 0x58, (byte) 0x20, (byte) 0x28, (byte) 0x7a, (byte) 0x01, (byte) 0x7f, (byte) 0x92, (byte) 0x90, (byte) 0x15, (byte) 0x6f, (byte) 0x65, (byte) 0x61, (byte) 0x8f, (byte) 0x13, (byte) 0x42, (byte) 0xeb, (byte) 0x96, (byte) 0x53, (byte) 0x2e, (byte) 0x57, (byte) 0x6a, (byte) 0x78, (byte) 0x86, (byte) 0x0a, (byte) 0x25, (byte) 0xcc, (byte) 0x3b, (byte) 0x09, (byte) 0xcb, (byte) 0xb0, (byte) 0x95, (byte) 0x93, (byte) 0xf7, (byte) 0xb9, (byte) 0x03, (byte) 0x81, (byte) 0xa2, (byte) 0x62, (byte) 0x69, (byte) 0x64, (byte) 0x58, (byte) 0x20, (byte) 0x7c, (byte) 0xe0, (byte) 0xbe});
        byte[] TEST_MULTIPLE_WRAPAROUNDS_CONT_FRAG_DATA = new byte[]{(byte) 0x13, (byte) 0x42, (byte) 0xeb, (byte) 0x96, (byte) 0x53, (byte) 0x2e, (byte) 0x57, (byte) 0x6a, (byte) 0x78, (byte) 0x86, (byte) 0x0a, (byte) 0x25, (byte) 0xcc, (byte) 0x3b, (byte) 0x09, (byte) 0xcb, (byte) 0xb0, (byte) 0x95, (byte) 0x93, (byte) 0xf7, (byte) 0xb9, (byte) 0x03, (byte) 0x81, (byte) 0xa2, (byte) 0x62, (byte) 0x69, (byte) 0x64, (byte) 0x58, (byte) 0x20, (byte) 0x7c, (byte) 0xe0, (byte) 0x1, (byte) 0xbe, (byte) 0xf2, (byte) 0x27, (byte) 0x8e, (byte) 0x10, (byte) 0xc4, (byte) 0x74, (byte) 0x75, (byte) 0xfd, (byte) 0xde, (byte) 0x1b, (byte) 0x4d, (byte) 0xc7, (byte) 0x20, (byte) 0x4d, (byte) 0xf9, (byte) 0x73, (byte) 0xd5, (byte) 0xa8, (byte) 0xa8, (byte) 0xae, (byte) 0xd1, (byte) 0x4f, (byte) 0x32, (byte) 0x27, (byte) 0x44, (byte) 0x24, (byte) 0xa8, (byte) 0xe5, (byte) 0x42, (byte) 0x64};
        List<Fragment> TEST_MULTIPLE_WRAPAROUND_FRAGMENTS = new ArrayList<>();
        TEST_MULTIPLE_WRAPAROUND_FRAGMENTS.add(TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG);
        // same continuation fragment is used multiple times
        for(int i = 0; i < 0xff; i++)
            TEST_MULTIPLE_WRAPAROUND_FRAGMENTS.add(new BaseContinuationFragment((byte) (i % 0x80), TEST_MULTIPLE_WRAPAROUNDS_CONT_FRAG_DATA ));

        defragmentationProvider.defragment(Flowable.fromIterable(TEST_MULTIPLE_WRAPAROUND_FRAGMENTS), TEST_MULTIPLE_WRAPAROUNDS_MAXLEN).test().assertNoErrors();
    }

    /*@Test
    public void transformsMultipleFragmentsWithMultipleWraparoundWithCorrectResult() {

    }*/

    @Test
    public void transformsMultipleFragmentsWithSmallestMaxLenWithNoErrors() throws BleException {
        int TEST_ONE_SMALLEST_MAXLEN_MAXLEN = 3;
        Fragment TEST_ONE_SMALLEST_MAXLEN_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x6a, new byte[]{});
        Fragment TEST_ONE_SMALLEST_MAXLEN_CONT_FRAG = new BaseContinuationFragment((byte) 0x02, new byte[]{(byte) 0xa3, (byte) 0x1});

        defragmentationProvider.defragment(Flowable.just(TEST_ONE_SMALLEST_MAXLEN_INIT_FRAG, TEST_ONE_SMALLEST_MAXLEN_CONT_FRAG), TEST_ONE_SMALLEST_MAXLEN_MAXLEN).test().assertNoErrors();
    }

    /*@Test
    public void transformsMultipleFragmentsWithSmallestMaxLenWithCorrectResult() {

    }*/

    @Test
    public void transformsFragmentsWithTooSmallMaxLenWithNoErrors() throws BleException {
        int TEST_TOO_SMALL_MAXLEN_MAXLEN = 2;
        Fragment TEST_TOO_SMALL_MAXLEN_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x6a, new byte[]{});
        Fragment TEST_TOO_SMALL_MAXLEN_CONT_FRAG = new BaseContinuationFragment((byte) 0x02, new byte[]{(byte) 0xa3, (byte) 0x1});

        defragmentationProvider.defragment(Flowable.just(TEST_TOO_SMALL_MAXLEN_INIT_FRAG, TEST_TOO_SMALL_MAXLEN_CONT_FRAG), TEST_TOO_SMALL_MAXLEN_MAXLEN).test().assertError(OtherException.class);
    }


    @Test
    public void transformsIncompleteFrameWithNoValues() throws BleException {
        int TEST_INCOMPLETE_FRAME_MAXLEN = 32;
        Fragment TEST_INCOMPLETE_FRAME_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x6a, new byte[]{(byte) 0x02, (byte) 0xa3, (byte) 0x01, (byte) 0x6b, (byte) 0x77, (byte) 0x65, (byte) 0x62, (byte) 0x61, (byte) 0x75, (byte) 0x74, (byte) 0x68, (byte) 0x6e, (byte) 0x2e, (byte) 0x69, (byte) 0x6f, (byte) 0x02, (byte) 0x58, (byte) 0x20, (byte) 0x28, (byte) 0x7a, (byte) 0x01, (byte) 0x7f, (byte) 0x92, (byte) 0x90, (byte) 0x15, (byte) 0x6f, (byte) 0x65, (byte) 0x61, (byte) 0x8f});
        Fragment TEST_INCOMPLETE_FRAME_CONT_FRAG = new BaseContinuationFragment((byte) 0, new byte[]{(byte) 0x13, (byte) 0x42, (byte) 0xeb, (byte) 0x96, (byte) 0x53, (byte) 0x2e, (byte) 0x57, (byte) 0x6a, (byte) 0x78, (byte) 0x86, (byte) 0x0a, (byte) 0x25, (byte) 0xcc, (byte) 0x3b, (byte) 0x09, (byte) 0xcb, (byte) 0xb0, (byte) 0x95, (byte) 0x93, (byte) 0xf7, (byte) 0xb9, (byte) 0x03, (byte) 0x81, (byte) 0xa2, (byte) 0x62, (byte) 0x69, (byte) 0x64, (byte) 0x58, (byte) 0x20, (byte) 0x7c, (byte) 0xe0});

        defragmentationProvider.defragment(Flowable.just(TEST_INCOMPLETE_FRAME_INIT_FRAG, TEST_INCOMPLETE_FRAME_CONT_FRAG), TEST_INCOMPLETE_FRAME_MAXLEN).test().assertNoValues();
    }


    @Test
    public void transformsMoreDataThanSpecifiedInHeaderWithErrors() throws BleException {
        int TEST_MORE_DATA_THAN_SPECIFIED_MAXLEN = 32;
        Fragment TEST_MORE_DATA_THAN_SPECIFIED_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x22, new byte[]{(byte) 0x02, (byte) 0xa3, (byte) 0x01, (byte) 0x6b, (byte) 0x77, (byte) 0x65, (byte) 0x62, (byte) 0x61, (byte) 0x75, (byte) 0x74, (byte) 0x68, (byte) 0x6e, (byte) 0x2e, (byte) 0x69, (byte) 0x6f, (byte) 0x02, (byte) 0x58, (byte) 0x20, (byte) 0x28, (byte) 0x7a, (byte) 0x01, (byte) 0x7f, (byte) 0x92, (byte) 0x90, (byte) 0x15, (byte) 0x6f, (byte) 0x65, (byte) 0x61, (byte) 0x8f});
        Fragment TEST_MORE_DATA_THAN_SPECIFIED_CONT_FRAG = new BaseContinuationFragment((byte) 0, new byte[]{(byte) 0x13, (byte) 0x42, (byte) 0xeb, (byte) 0x96, (byte) 0x53, (byte) 0x2e, (byte) 0x57, (byte) 0x6a, (byte) 0x78, (byte) 0x86, (byte) 0x0a, (byte) 0x25, (byte) 0xcc, (byte) 0x3b, (byte) 0x09, (byte) 0xcb, (byte) 0xb0, (byte) 0x95, (byte) 0x93, (byte) 0xf7, (byte) 0xb9, (byte) 0x03, (byte) 0x81, (byte) 0xa2, (byte) 0x62, (byte) 0x69, (byte) 0x64, (byte) 0x58, (byte) 0x20, (byte) 0x7c, (byte) 0xe0});

        defragmentationProvider.defragment(Flowable.just(TEST_MORE_DATA_THAN_SPECIFIED_INIT_FRAG, TEST_MORE_DATA_THAN_SPECIFIED_CONT_FRAG), TEST_MORE_DATA_THAN_SPECIFIED_MAXLEN).test().assertError(InvalidLengthException.class);
    }

    // test transformation of multiple frames at once

}
