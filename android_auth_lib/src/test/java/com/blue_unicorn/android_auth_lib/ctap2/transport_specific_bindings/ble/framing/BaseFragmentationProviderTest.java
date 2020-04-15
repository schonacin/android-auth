package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import android.util.Base64;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.BleException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.OtherException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseContinuationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseInitializationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@RunWith(RobolectricTestRunner.class)
public class BaseFragmentationProviderTest {

    private BaseFragmentationProvider fragmentationProvider;

    private static final String DATA = "AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==";

    @Before
    public void setUp() {
        fragmentationProvider = new BaseFragmentationProvider();
    }

    @Test
    public void transformsFrameToSingleFragmentWithoutErrors() throws BleException {
        int TEST_FRAME_TO_SINGLE_FRAGMENT_MAXLEN = 1024;
        Frame TEST_FRAME_TO_SINGLE_FRAGMENT_FRAME = new BaseFrame((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        fragmentationProvider.fragment(Single.just(TEST_FRAME_TO_SINGLE_FRAGMENT_FRAME), TEST_FRAME_TO_SINGLE_FRAGMENT_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsFrameToSingleFragmentWithCorrectResult() throws BleException {
        int TEST_FRAME_TO_SINGLE_FRAGMENT_MAXLEN = 1024;
        Frame TEST_FRAME_TO_SINGLE_FRAGMENT_FRAME = new BaseFrame((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));

        Fragment TEST_FRAME_TO_SINGLE_FRAGMENT_RESULT_INIT_FRAG = new BaseInitializationFragment(TEST_FRAME_TO_SINGLE_FRAGMENT_FRAME.getCMDSTAT(), TEST_FRAME_TO_SINGLE_FRAGMENT_FRAME.getHLEN(), TEST_FRAME_TO_SINGLE_FRAGMENT_FRAME.getLLEN(), TEST_FRAME_TO_SINGLE_FRAGMENT_FRAME.getDATA());
        fragmentationProvider.fragment(Single.just(TEST_FRAME_TO_SINGLE_FRAGMENT_FRAME), TEST_FRAME_TO_SINGLE_FRAGMENT_MAXLEN).test().assertValue(TEST_FRAME_TO_SINGLE_FRAGMENT_RESULT_INIT_FRAG);
    }

    @Test
    public void transformsFrameToMultipleFragmentsWithoutWraparoundWithoutErrors() throws BleException {
        int TEST_FRAME_TO_MULTIPLE_FRAGMENTS_MAXLEN = 32;
        Frame TEST_FRAME_TO_MULTIPLE_FRAGMENTS_FRAME = new BaseFrame((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        fragmentationProvider.fragment(Single.just(TEST_FRAME_TO_MULTIPLE_FRAGMENTS_FRAME), TEST_FRAME_TO_MULTIPLE_FRAGMENTS_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsFrameToMultipleFragmentsWithoutWraparoundWithCorrectResult() throws BleException {
        int TEST_FRAME_TO_MULTIPLE_FRAGMENTS_MAXLEN = 32;
        Frame TEST_FRAME_TO_MULTIPLE_FRAGMENTS_FRAME = new BaseFrame((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT));

        Fragment TEST_FRAME_TO_MULTIPLE_FRAGMENTS_RESULT_INIT_FRAG = new BaseInitializationFragment(TEST_FRAME_TO_MULTIPLE_FRAGMENTS_FRAME.getCMDSTAT(), TEST_FRAME_TO_MULTIPLE_FRAGMENTS_FRAME.getHLEN(), TEST_FRAME_TO_MULTIPLE_FRAGMENTS_FRAME.getLLEN(), Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8=", Base64.DEFAULT));
        Fragment TEST_FRAME_TO_MULTIPLE_FRAGMENTS_RESULT_CONT_FRAG_0 = new BaseContinuationFragment((byte) 0, Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84A==", Base64.DEFAULT));
        Fragment TEST_FRAME_TO_MULTIPLE_FRAGMENTS_RESULT_CONT_FRAG_1 = new BaseContinuationFragment((byte) 1, Base64.decode("vvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZA==", Base64.DEFAULT));
        Fragment TEST_FRAME_TO_MULTIPLE_FRAGMENTS_RESULT_CONT_FRAG_2 = new BaseContinuationFragment((byte) 2, Base64.decode("dHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));
        fragmentationProvider.fragment(Single.just(TEST_FRAME_TO_MULTIPLE_FRAGMENTS_FRAME), TEST_FRAME_TO_MULTIPLE_FRAGMENTS_MAXLEN).test().assertValues(TEST_FRAME_TO_MULTIPLE_FRAGMENTS_RESULT_INIT_FRAG, TEST_FRAME_TO_MULTIPLE_FRAGMENTS_RESULT_CONT_FRAG_0, TEST_FRAME_TO_MULTIPLE_FRAGMENTS_RESULT_CONT_FRAG_1, TEST_FRAME_TO_MULTIPLE_FRAGMENTS_RESULT_CONT_FRAG_2);
    }

    @Test
    public void transformsFrameToMultipleFragmentsWithSingleWraparoundWithoutErrors() throws BleException {
        int TEST_FRAME_TO_ONE_WRAPAROUND_MAXLEN = 24;
        byte[] TEST_FRAME_TO_ONE_WRAPAROUND_FRAME_DATA = new byte[0xc64];
        System.arraycopy(Base64.decode(DATA, Base64.DEFAULT), 0, TEST_FRAME_TO_ONE_WRAPAROUND_FRAME_DATA, 0, 21);
        // same continuation fragment is used multiple times
        for(int i = 1; i < 0x8A; i++)
            System.arraycopy(Base64.decode(DATA, Base64.DEFAULT), 0, TEST_FRAME_TO_ONE_WRAPAROUND_FRAME_DATA, 21 + 23 * (i - 1), 23);
        Frame TEST_FRAME_TO_ONE_WRAPAROUND_FRAME = new BaseFrame((byte) 0x83, (byte) 0x0c, (byte) 0x64, TEST_FRAME_TO_ONE_WRAPAROUND_FRAME_DATA);
        fragmentationProvider.fragment(Single.just(TEST_FRAME_TO_ONE_WRAPAROUND_FRAME), TEST_FRAME_TO_ONE_WRAPAROUND_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsFrameToMultipleFragmentsWithSingleWraparoundWithCorrectResult() throws BleException {
        int TEST_FRAME_TO_ONE_WRAPAROUND_MAXLEN = 24;
        byte[] TEST_FRAME_TO_ONE_WRAPAROUND_FRAME_DATA = new byte[0xc64];
        System.arraycopy(Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoB", Base64.DEFAULT), 0, TEST_FRAME_TO_ONE_WRAPAROUND_FRAME_DATA, 0, 21);
        // same continuation fragment is used multiple times
        for(int i = 1; i < 0x8A; i++)
            System.arraycopy(Base64.decode("f5KQFW9lYY8TQuuWUy5XaniGCiXMOwk=", Base64.DEFAULT), 0, TEST_FRAME_TO_ONE_WRAPAROUND_FRAME_DATA, 21 + 23 * (i - 1), 23);
        Frame TEST_FRAME_TO_ONE_WRAPAROUND_FRAME = new BaseFrame((byte) 0x83, (byte) 0x0c, (byte) 0x64, TEST_FRAME_TO_ONE_WRAPAROUND_FRAME_DATA);

        Fragment TEST_FRAME_TO_ONE_WRAPAROUND_RESULT_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x0c, (byte) 0x64, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoB", Base64.DEFAULT));
        byte[] TEST_FRAME_TO_ONE_WRAPAROUND_RESULT_CONT_FRAG_DATA = Base64.decode("f5KQFW9lYY8TQuuWUy5XaniGCiXMOwk=", Base64.DEFAULT);
        List<Fragment> TEST_FRAME_TO_ONE_WRAPAROUND_FRAGMENTS = new ArrayList<>();
        TEST_FRAME_TO_ONE_WRAPAROUND_FRAGMENTS.add(TEST_FRAME_TO_ONE_WRAPAROUND_RESULT_INIT_FRAG);
        for(int i = 0; i < 0x89; i++)
            TEST_FRAME_TO_ONE_WRAPAROUND_FRAGMENTS.add(new BaseContinuationFragment((byte) (i % 0x80), TEST_FRAME_TO_ONE_WRAPAROUND_RESULT_CONT_FRAG_DATA));
        fragmentationProvider.fragment(Single.just(TEST_FRAME_TO_ONE_WRAPAROUND_FRAME), TEST_FRAME_TO_ONE_WRAPAROUND_MAXLEN).test().assertValueSequence(TEST_FRAME_TO_ONE_WRAPAROUND_FRAGMENTS);
    }

    @Test
    public void transformsFrameToMultipleFragmentsWithMultipleWraparoundWithoutErrors() throws BleException {
        int TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_MAXLEN = 64;
        byte[] TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAME_DATA = new byte[0x3efe];
        System.arraycopy(Base64.decode(DATA, Base64.DEFAULT), 0, TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAME_DATA, 0, 61);
        for(int i = 1; i < 0x100; i++)
            System.arraycopy(Base64.decode(DATA, Base64.DEFAULT), 0, TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAME_DATA, 61 + 63 * (i - 1), 63);
        Frame TEST_FRAME_TO_ONE_WRAPAROUND_FRAME = new BaseFrame((byte) 0x83, (byte) 0x3e, (byte) 0xfe, TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAME_DATA);
        fragmentationProvider.fragment(Single.just(TEST_FRAME_TO_ONE_WRAPAROUND_FRAME), TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsFrameToMultipleFragmentsWithMultipleWraparoundWithCorrectResult() throws BleException {
        int TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_MAXLEN = 64;
        byte[] TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAME_DATA = new byte[0x3efe];
        System.arraycopy(Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvg==", Base64.DEFAULT), 0, TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAME_DATA, 0, 61);
        for(int i = 1; i < 0x100; i++)
            System.arraycopy(Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84AG+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJk", Base64.DEFAULT), 0, TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAME_DATA, 61 + 63 * (i - 1), 63);
        Frame TEST_FRAME_TO_ONE_WRAPAROUND_FRAME = new BaseFrame((byte) 0x83, (byte) 0x3e, (byte) 0xfe, TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAME_DATA);

        Fragment TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_RESULT_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x3e, (byte) 0xfe, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvg==", Base64.DEFAULT));
        byte[] TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_RESULT_CONT_FRAG_DATA = Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84AG+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJk", Base64.DEFAULT);
        List<Fragment> TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAGMENTS = new ArrayList<>();
        TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAGMENTS.add(TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_RESULT_INIT_FRAG);
        // same continuation fragment is used multiple times
        for(int i = 0; i < 0xff; i++)
            TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAGMENTS.add(new BaseContinuationFragment((byte) (i % 0x80), TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_RESULT_CONT_FRAG_DATA ));
        fragmentationProvider.fragment(Single.just(TEST_FRAME_TO_ONE_WRAPAROUND_FRAME), TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_MAXLEN).test().assertValueSequence(TEST_FRAME_TO_MULTIPLE_WRAPAROUNDS_FRAGMENTS);
    }

    @Test
    public void transformsFrameWithSmallestMaxLenWithoutErrors() throws BleException {
        int TEST_FRAME_WITH_SMALLEST_MAXLEN_MAXLEN = 3;
        Frame TEST_FRAME_WITH_SMALLEST_MAXLEN_FRAME = new BaseFrame((byte) 0x83, (byte) 0x00, (byte) 0x02, new byte[]{(byte) 0xa3, (byte) 0x1});
        fragmentationProvider.fragment(Single.just(TEST_FRAME_WITH_SMALLEST_MAXLEN_FRAME), TEST_FRAME_WITH_SMALLEST_MAXLEN_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsFrameWithSmallestMaxLenWithCorrectResult() throws BleException {
        int TEST_FRAME_WITH_SMALLEST_MAXLEN_MAXLEN = 3;
        Frame TEST_FRAME_WITH_SMALLEST_MAXLEN_FRAME = new BaseFrame((byte) 0x83, (byte) 0x00, (byte) 0x02, new byte[]{(byte) 0xa3, (byte) 0x1});

        Fragment TEST_FRAME_WITH_SMALLEST_MAXLEN_RESULT_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x02, new byte[]{});
        Fragment TEST_FRAME_WITH_SMALLEST_MAXLEN_RESULT_CONT_FRAG = new BaseContinuationFragment((byte) 0x00, new byte[]{(byte) 0xa3, (byte) 0x1});
        fragmentationProvider.fragment(Single.just(TEST_FRAME_WITH_SMALLEST_MAXLEN_FRAME), TEST_FRAME_WITH_SMALLEST_MAXLEN_MAXLEN).test().assertValues(TEST_FRAME_WITH_SMALLEST_MAXLEN_RESULT_INIT_FRAG, TEST_FRAME_WITH_SMALLEST_MAXLEN_RESULT_CONT_FRAG);
    }

    @Test
    public void transformsMultipleFramesWithCorrectResult() throws BleException {
        int TEST_MULTIPLE_FRAMES_MAXLEN = 109;
        Frame TEST_MULTIPLE_FRAMES_FRAME_0 = new BaseFrame((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT));
        Frame TEST_MULTIPLE_FRAMES_FRAME_1 = new BaseFrame((byte) 0x83, (byte) 0x01, (byte) 0x42, Base64.decode("AaMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleSIqJaMBD1LiGmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleSNqAqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT));
        byte[] TEST_MULTIPLE_FRAMES_FRAME_2_DATA = new byte[0x366a];
        System.arraycopy(Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT), 0, TEST_MULTIPLE_FRAMES_FRAME_2_DATA, 0, 106);
        for(int i = 0; i < 0x80; i++)
            System.arraycopy(Base64.decode("IiolowEPUuIaYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT), 0, TEST_MULTIPLE_FRAMES_FRAME_2_DATA, 106 + i * 108, 108);
        Frame TEST_MULTIPLE_FRAMES_FRAME_2 = new BaseFrame((byte) 0x83, (byte) 0x36, (byte) 0x6a, TEST_MULTIPLE_FRAMES_FRAME_2_DATA);

        List<List<Fragment>> TEST_MULTIPLE_FRAMES_FRAME_FRAGMENTS = new ArrayList<>();
        List<Fragment> TEST_MULTIPLE_FRAMES_FRAME_0_FRAGMENTS = new ArrayList<>();
        Fragment TEST_MULTIPLE_FRAMES_FRAME_0_RESULT_INIT_FRAG = new BaseInitializationFragment((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT));
        TEST_MULTIPLE_FRAMES_FRAME_0_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_0_RESULT_INIT_FRAG);
        TEST_MULTIPLE_FRAMES_FRAME_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_0_FRAGMENTS);
        List<Fragment> TEST_MULTIPLE_FRAMES_FRAME_1_FRAGMENTS = new ArrayList<>();
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_RESULT_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x01, (byte) 0x42, Base64.decode("AaMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT));
        TEST_MULTIPLE_FRAMES_FRAME_1_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_RESULT_INIT_FRAG);
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_RESULT_CONT_FRAG_0 = new BaseContinuationFragment((byte) 0, Base64.decode("IiolowEPUuIaYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));
        TEST_MULTIPLE_FRAMES_FRAME_1_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_RESULT_CONT_FRAG_0);
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_RESULT_CONT_FRAG_1 = new BaseContinuationFragment((byte) 1, Base64.decode("I2oCowFrd2ViYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));
        TEST_MULTIPLE_FRAMES_FRAME_1_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_RESULT_CONT_FRAG_1);
        TEST_MULTIPLE_FRAMES_FRAME_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_FRAGMENTS);
        List<Fragment> TEST_MULTIPLE_FRAMES_FRAME_2_FRAGMENTS = new ArrayList<>();
        Fragment TEST_MULTIPLE_FRAMES_FRAME_2_RESULT_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x36, (byte) 0x6a, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT));
        TEST_MULTIPLE_FRAMES_FRAME_2_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_2_RESULT_INIT_FRAG);
        byte[] TEST_MULTIPLE_FRAMES_FRAME_2_RESULT_CONT_FRAG_DATA = Base64.decode("IiolowEPUuIaYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT);
        for(int i = 0; i < 0x80; i++)
            TEST_MULTIPLE_FRAMES_FRAME_2_FRAGMENTS.add(new BaseContinuationFragment((byte) i, TEST_MULTIPLE_FRAMES_FRAME_2_RESULT_CONT_FRAG_DATA));
        TEST_MULTIPLE_FRAMES_FRAME_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_2_FRAGMENTS);

        Flowable.just(TEST_MULTIPLE_FRAMES_FRAME_0, TEST_MULTIPLE_FRAMES_FRAME_1, TEST_MULTIPLE_FRAMES_FRAME_2).zipWith(Flowable.fromIterable(TEST_MULTIPLE_FRAMES_FRAME_FRAGMENTS), (frame, fragments) -> {
            fragmentationProvider.fragment(Single.just(frame), TEST_MULTIPLE_FRAMES_MAXLEN).test().assertValueSequence(fragments); return new Object(); // zipWith requires some return type
        }).test().assertValueCount(TEST_MULTIPLE_FRAMES_FRAME_FRAGMENTS.size());
    }

    @Test
    public void transformsFrameWithTooSmallMaxLenWithErrors() throws BleException {
        int TEST_TOO_SMALL_MAXLEN_MAXLEN = 2;
        Frame TEST_TOO_SMALL_MAXLEN_FRAME = new BaseFrame((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        
        fragmentationProvider.fragment(Single.just(TEST_TOO_SMALL_MAXLEN_FRAME), TEST_TOO_SMALL_MAXLEN_MAXLEN).test().assertError(OtherException.class);
    }
}
