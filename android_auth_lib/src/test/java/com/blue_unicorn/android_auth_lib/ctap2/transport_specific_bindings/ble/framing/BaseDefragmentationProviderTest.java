package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import android.util.Base64;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.BleException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.InvalidLengthException;
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

@RunWith(RobolectricTestRunner.class)
public class BaseDefragmentationProviderTest {

    private static final String DATA = "AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==";
    private BaseDefragmentationProvider defragmentationProvider;

    @Before
    public void setUp() {
        defragmentationProvider = new BaseDefragmentationProvider();
    }

    @Test
    public void transformsSingleFragmentWithoutErrors() throws BleException {
        int TEST_SINGLE_FRAGMENT_MAXLEN = 1024;
        Fragment TEST_SINGLE_FRAGMENT_INIT_FRAG = new BaseInitializationFragment((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));

        defragmentationProvider.defragment(Flowable.just(TEST_SINGLE_FRAGMENT_INIT_FRAG), TEST_SINGLE_FRAGMENT_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsSingleFragmentWithCorrectResult() throws BleException {
        int TEST_SINGLE_FRAGMENT_MAXLEN = 1024;
        Fragment TEST_SINGLE_FRAGMENT_INIT_FRAG = new BaseInitializationFragment((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));

        Frame TEST_SINGLE_FRAGMENT_RESULT_FRAME = new BaseFrame(((BaseInitializationFragment) TEST_SINGLE_FRAGMENT_INIT_FRAG).getCMD(), ((BaseInitializationFragment) TEST_SINGLE_FRAGMENT_INIT_FRAG).getHLEN(), ((BaseInitializationFragment) TEST_SINGLE_FRAGMENT_INIT_FRAG).getLLEN(), TEST_SINGLE_FRAGMENT_INIT_FRAG.getDATA());
        defragmentationProvider.defragment(Flowable.just(TEST_SINGLE_FRAGMENT_INIT_FRAG), TEST_SINGLE_FRAGMENT_MAXLEN).test().assertValue(TEST_SINGLE_FRAGMENT_RESULT_FRAME);
    }

    @Test
    public void transformsMultipleFragmentsOutOffOrderWithoutWraparoundWithoutErrors() throws BleException {
        int TEST_MULTIPLE_FRAGMENTS_MAXLEN = 32;
        Fragment TEST_MULTIPLE_FRAGMENTS_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x6a, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8=", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_0 = new BaseContinuationFragment((byte) 0, Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84A==", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_1 = new BaseContinuationFragment((byte) 1, Base64.decode("vvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZA==", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_2 = new BaseContinuationFragment((byte) 2, Base64.decode("dHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));

        defragmentationProvider.defragment(Flowable.just(TEST_MULTIPLE_FRAGMENTS_INIT_FRAG, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_1, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_2, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_0), TEST_MULTIPLE_FRAGMENTS_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsMultipleFragmentsWithoutWraparoundWithCorrectResult() throws BleException {
        int TEST_MULTIPLE_FRAGMENTS_MAXLEN = 32;
        Fragment TEST_MULTIPLE_FRAGMENTS_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x6a, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8=", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_0 = new BaseContinuationFragment((byte) 0, Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84A==", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_1 = new BaseContinuationFragment((byte) 1, Base64.decode("vvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZA==", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_2 = new BaseContinuationFragment((byte) 2, Base64.decode("dHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));

        byte[] TEST_MULTIPLE_FRAGMENTS_RESULT_FRAME_DATA = new byte[0x6a];
        System.arraycopy(TEST_MULTIPLE_FRAGMENTS_INIT_FRAG.getDATA(), 0, TEST_MULTIPLE_FRAGMENTS_RESULT_FRAME_DATA, 0, 29);
        System.arraycopy(TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_0.getDATA(), 0, TEST_MULTIPLE_FRAGMENTS_RESULT_FRAME_DATA, 29, 31);
        System.arraycopy(TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_1.getDATA(), 0, TEST_MULTIPLE_FRAGMENTS_RESULT_FRAME_DATA, 60, 31);
        System.arraycopy(TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_2.getDATA(), 0, TEST_MULTIPLE_FRAGMENTS_RESULT_FRAME_DATA, 91, 15);
        Frame TEST_MULTIPLE_FRAGMENTS_RESULT_FRAME = new BaseFrame(((BaseInitializationFragment) TEST_MULTIPLE_FRAGMENTS_INIT_FRAG).getCMD(), ((BaseInitializationFragment) TEST_MULTIPLE_FRAGMENTS_INIT_FRAG).getHLEN(), ((BaseInitializationFragment) TEST_MULTIPLE_FRAGMENTS_INIT_FRAG).getLLEN(), TEST_MULTIPLE_FRAGMENTS_RESULT_FRAME_DATA);
        defragmentationProvider.defragment(Flowable.just(TEST_MULTIPLE_FRAGMENTS_INIT_FRAG, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_1, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_2, TEST_MULTIPLE_FRAGMENTS_CONT_FRAG_0), TEST_MULTIPLE_FRAGMENTS_MAXLEN).test().assertValue(TEST_MULTIPLE_FRAGMENTS_RESULT_FRAME);
    }

    @Test
    public void transformsMultipleFragmentsWithSingleWraparoundWithoutErrors() throws BleException {
        int TEST_ONE_WRAPAROUND_MAXLEN = 24;
        Fragment TEST_ONE_WRAPAROUND_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x0c, (byte) 0x64, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoB", Base64.DEFAULT));
        byte[] TEST_ONE_WRAPAROUND_CONT_FRAG_DATA = Base64.decode("f5KQFW9lYY8TQuuWUy5XaniGCiXMOwk=", Base64.DEFAULT);
        List<Fragment> TEST_ONE_WRAPAROUND_FRAGMENTS = new ArrayList<>();
        TEST_ONE_WRAPAROUND_FRAGMENTS.add(TEST_ONE_WRAPAROUND_INIT_FRAG);
        // same continuation fragment is used multiple times
        for (int i = 0; i < 0x89; i++)
            TEST_ONE_WRAPAROUND_FRAGMENTS.add(new BaseContinuationFragment((byte) (i % 0x80), TEST_ONE_WRAPAROUND_CONT_FRAG_DATA));

        defragmentationProvider.defragment(Flowable.fromIterable(TEST_ONE_WRAPAROUND_FRAGMENTS), TEST_ONE_WRAPAROUND_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsMultipleFragmentsWithSingleWraparoundWithCorrectResult() throws BleException {
        int TEST_ONE_WRAPAROUND_MAXLEN = 24;
        Fragment TEST_ONE_WRAPAROUND_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x0c, (byte) 0x64, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoB", Base64.DEFAULT));
        byte[] TEST_ONE_WRAPAROUND_CONT_FRAG_DATA = Base64.decode("f5KQFW9lYY8TQuuWUy5XaniGCiXMOwk=", Base64.DEFAULT);
        List<Fragment> TEST_ONE_WRAPAROUND_FRAGMENTS = new ArrayList<>();
        TEST_ONE_WRAPAROUND_FRAGMENTS.add(TEST_ONE_WRAPAROUND_INIT_FRAG);
        // same continuation fragment is used multiple times
        for (int i = 0; i < 0x89; i++)
            TEST_ONE_WRAPAROUND_FRAGMENTS.add(new BaseContinuationFragment((byte) (i % 0x80), TEST_ONE_WRAPAROUND_CONT_FRAG_DATA));

        byte[] TEST_ONE_WRAPAROUND_RESULT_FRAME_DATA = new byte[0xc64];
        System.arraycopy(TEST_ONE_WRAPAROUND_INIT_FRAG.getDATA(), 0, TEST_ONE_WRAPAROUND_RESULT_FRAME_DATA, 0, 21);
        for (int i = 1; i < TEST_ONE_WRAPAROUND_FRAGMENTS.size(); i++)
            System.arraycopy(TEST_ONE_WRAPAROUND_FRAGMENTS.get(i).getDATA(), 0, TEST_ONE_WRAPAROUND_RESULT_FRAME_DATA, 21 + 23 * (i - 1), 23);
        Frame TEST_ONE_WRAPAROUND_RESULT_FRAME = new BaseFrame(((BaseInitializationFragment) TEST_ONE_WRAPAROUND_INIT_FRAG).getCMD(), ((BaseInitializationFragment) TEST_ONE_WRAPAROUND_INIT_FRAG).getHLEN(), ((BaseInitializationFragment) TEST_ONE_WRAPAROUND_INIT_FRAG).getLLEN(), TEST_ONE_WRAPAROUND_RESULT_FRAME_DATA);
        defragmentationProvider.defragment(Flowable.fromIterable(TEST_ONE_WRAPAROUND_FRAGMENTS), TEST_ONE_WRAPAROUND_MAXLEN).test().assertValue(TEST_ONE_WRAPAROUND_RESULT_FRAME);
    }


    @Test
    public void transformsMultipleFragmentsWithMultipleWraparoundWithoutErrors() throws BleException {
        int TEST_MULTIPLE_WRAPAROUNDS_MAXLEN = 64;
        Fragment TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x3e, (byte) 0xfe, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvg==", Base64.DEFAULT));
        byte[] TEST_MULTIPLE_WRAPAROUNDS_CONT_FRAG_DATA = Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84AG+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJk", Base64.DEFAULT);
        List<Fragment> TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS = new ArrayList<>();
        TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS.add(TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG);
        // same continuation fragment is used multiple times
        for (int i = 0; i < 0xff; i++)
            TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS.add(new BaseContinuationFragment((byte) (i % 0x80), TEST_MULTIPLE_WRAPAROUNDS_CONT_FRAG_DATA));

        defragmentationProvider.defragment(Flowable.fromIterable(TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS), TEST_MULTIPLE_WRAPAROUNDS_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsMultipleFragmentsWithMultipleWraparoundWithCorrectResult() throws BleException {
        int TEST_MULTIPLE_WRAPAROUNDS_MAXLEN = 64;
        Fragment TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x3e, (byte) 0xfe, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvg==", Base64.DEFAULT));
        byte[] TEST_MULTIPLE_WRAPAROUNDS_CONT_FRAG_DATA = Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84AG+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJk", Base64.DEFAULT);
        List<Fragment> TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS = new ArrayList<>();
        TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS.add(TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG);
        // same continuation fragment is used multiple times
        for (int i = 0; i < 0xff; i++)
            TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS.add(new BaseContinuationFragment((byte) (i % 0x80), TEST_MULTIPLE_WRAPAROUNDS_CONT_FRAG_DATA));

        byte[] TEST_MULTIPLE_WRAPAROUNDS_RESULT_FRAME_DATA = new byte[0x3efe];
        System.arraycopy(TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG.getDATA(), 0, TEST_MULTIPLE_WRAPAROUNDS_RESULT_FRAME_DATA, 0, 61);
        for (int i = 1; i < TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS.size(); i++)
            System.arraycopy(TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS.get(i).getDATA(), 0, TEST_MULTIPLE_WRAPAROUNDS_RESULT_FRAME_DATA, 61 + 63 * (i - 1), 63);
        Frame TEST_ONE_WRAPAROUND_RESULT_FRAME = new BaseFrame(((BaseInitializationFragment) TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG).getCMD(), ((BaseInitializationFragment) TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG).getHLEN(), ((BaseInitializationFragment) TEST_MULTIPLE_WRAPAROUNDS_INIT_FRAG).getLLEN(), TEST_MULTIPLE_WRAPAROUNDS_RESULT_FRAME_DATA);
        defragmentationProvider.defragment(Flowable.fromIterable(TEST_MULTIPLE_WRAPAROUNDS_FRAGMENTS), TEST_MULTIPLE_WRAPAROUNDS_MAXLEN).test().assertValue(TEST_ONE_WRAPAROUND_RESULT_FRAME);
    }

    @Test
    public void transformsMultipleFragmentsWithSmallestMaxLenWithoutErrors() throws BleException {
        int TEST_ONE_SMALLEST_MAXLEN_MAXLEN = 3;
        Fragment TEST_ONE_SMALLEST_MAXLEN_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x02, new byte[]{});
        Fragment TEST_ONE_SMALLEST_MAXLEN_CONT_FRAG = new BaseContinuationFragment((byte) 0x00, new byte[]{(byte) 0xa3, (byte) 0x1});

        defragmentationProvider.defragment(Flowable.just(TEST_ONE_SMALLEST_MAXLEN_INIT_FRAG, TEST_ONE_SMALLEST_MAXLEN_CONT_FRAG), TEST_ONE_SMALLEST_MAXLEN_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsMultipleFragmentsWithSmallestMaxLenWithCorrectResult() throws BleException {
        int TEST_ONE_SMALLEST_MAXLEN_MAXLEN = 3;
        Fragment TEST_ONE_SMALLEST_MAXLEN_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x02, new byte[]{});
        Fragment TEST_ONE_SMALLEST_MAXLEN_CONT_FRAG = new BaseContinuationFragment((byte) 0x00, new byte[]{(byte) 0xa3, (byte) 0x1});

        Frame TEST_ONE_SMALLEST_MAXLEN_RESULT_FRAME = new BaseFrame((byte) 0x83, (byte) 0x00, (byte) 0x02, new byte[]{(byte) 0xa3, (byte) 0x1});
        defragmentationProvider.defragment(Flowable.just(TEST_ONE_SMALLEST_MAXLEN_INIT_FRAG, TEST_ONE_SMALLEST_MAXLEN_CONT_FRAG), TEST_ONE_SMALLEST_MAXLEN_MAXLEN).test().assertValue(TEST_ONE_SMALLEST_MAXLEN_RESULT_FRAME);
    }

    @Test
    public void transformsMultipleFramesWithoutErrors() throws BleException {
        int TEST_MULTIPLE_FRAMES_MAXLEN = 109;
        Fragment TEST_MULTIPLE_FRAMES_FRAME_0_INIT_FRAG = new BaseInitializationFragment((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x01, (byte) 0x42, Base64.decode("AaMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_0 = new BaseContinuationFragment((byte) 0, Base64.decode("IiolowEPUuIaYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_1 = new BaseContinuationFragment((byte) 1, Base64.decode("I2oCowFrd2ViYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAMES_FRAME_2_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x36, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        byte[] TEST_MULTIPLE_FRAMES_FRAME_2_CONT_FRAG_DATA = Base64.decode("IiolowEPUuIaYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT);
        List<Fragment> TEST_MULTIPLE_FRAMES_FRAGMENTS = new ArrayList<>();
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_0_INIT_FRAG);
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_INIT_FRAG);
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_0);
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_1);
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_2_INIT_FRAG);
        for (int i = 0; i < 0x80; i++)
            TEST_MULTIPLE_FRAMES_FRAGMENTS.add(new BaseContinuationFragment((byte) i, TEST_MULTIPLE_FRAMES_FRAME_2_CONT_FRAG_DATA));

        defragmentationProvider.defragment(Flowable.fromIterable(TEST_MULTIPLE_FRAMES_FRAGMENTS), TEST_MULTIPLE_FRAMES_MAXLEN).test().assertNoErrors();
    }

    @Test
    public void transformsMultipleFramesWithCorrectResult() throws BleException {
        int TEST_MULTIPLE_FRAMES_MAXLEN = 109;
        Fragment TEST_MULTIPLE_FRAMES_FRAME_0_INIT_FRAG = new BaseInitializationFragment((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x01, (byte) 0x42, Base64.decode("AaMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8TQuuWUy5XaniGCiXMOwnLsJWT97kDgaJiaWRYIHzgvvInjhDEdHX93htNxyBN+XPVqKiu0U8yJ0QkqOVCZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_0 = new BaseContinuationFragment((byte) 0, Base64.decode("IiolowEPUuIaYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_1 = new BaseContinuationFragment((byte) 1, Base64.decode("I2oCowFrd2ViYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT));
        Fragment TEST_MULTIPLE_FRAMES_FRAME_2_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x36, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));
        byte[] TEST_MULTIPLE_FRAMES_FRAME_2_CONT_FRAG_DATA = Base64.decode("IiolowEPUuIaYXV0aG4uaW8CWCAoegF/kpAVb2VhjxNC65ZTLldqeIYKJcw7CcuwlZP3uQOBomJpZFggfOC+8ieOEMR0df3eG03HIE35c9WoqK7RTzInRCSo5UJkdHlwZWpwdWJsaWMta2V5", Base64.DEFAULT);
        List<Fragment> TEST_MULTIPLE_FRAMES_FRAGMENTS = new ArrayList<>();
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_0_INIT_FRAG);
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_INIT_FRAG);
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_0);
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_1);
        TEST_MULTIPLE_FRAMES_FRAGMENTS.add(TEST_MULTIPLE_FRAMES_FRAME_2_INIT_FRAG);
        for (int i = 0; i < 0x80; i++)
            TEST_MULTIPLE_FRAMES_FRAGMENTS.add(new BaseContinuationFragment((byte) i, TEST_MULTIPLE_FRAMES_FRAME_2_CONT_FRAG_DATA));

        Frame TEST_MULTIPLE_FRAMES_RESULT_FRAME_0 = new BaseFrame(((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_0_INIT_FRAG).getCMD(), ((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_0_INIT_FRAG).getHLEN(), ((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_0_INIT_FRAG).getLLEN(), TEST_MULTIPLE_FRAMES_FRAME_0_INIT_FRAG.getDATA());
        byte[] TEST_MULTIPLE_FRAMES_RESULT_FRAME_1_DATA = new byte[0x142];
        System.arraycopy(TEST_MULTIPLE_FRAMES_FRAME_1_INIT_FRAG.getDATA(), 0, TEST_MULTIPLE_FRAMES_RESULT_FRAME_1_DATA, 0, 106);
        System.arraycopy(TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_0.getDATA(), 0, TEST_MULTIPLE_FRAMES_RESULT_FRAME_1_DATA, 106, 108);
        System.arraycopy(TEST_MULTIPLE_FRAMES_FRAME_1_CONT_FRAG_1.getDATA(), 0, TEST_MULTIPLE_FRAMES_RESULT_FRAME_1_DATA, 214, 108);
        Frame TEST_MULTIPLE_FRAMES_RESULT_FRAME_1 = new BaseFrame(((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_1_INIT_FRAG).getCMD(), ((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_1_INIT_FRAG).getHLEN(), ((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_1_INIT_FRAG).getLLEN(), TEST_MULTIPLE_FRAMES_RESULT_FRAME_1_DATA);
        byte[] TEST_MULTIPLE_FRAMES_RESULT_FRAME_2_DATA = new byte[0x366a];
        System.arraycopy(TEST_MULTIPLE_FRAMES_FRAME_2_INIT_FRAG.getDATA(), 0, TEST_MULTIPLE_FRAMES_RESULT_FRAME_2_DATA, 0, 106);
        for (int i = 0; i < 0x80; i++)
            System.arraycopy(TEST_MULTIPLE_FRAMES_FRAME_2_CONT_FRAG_DATA, 0, TEST_MULTIPLE_FRAMES_RESULT_FRAME_2_DATA, 106 + i * 108, 108);
        Frame TEST_MULTIPLE_FRAMES_RESULT_FRAME_2 = new BaseFrame(((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_2_INIT_FRAG).getCMD(), ((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_2_INIT_FRAG).getHLEN(), ((BaseInitializationFragment) TEST_MULTIPLE_FRAMES_FRAME_2_INIT_FRAG).getLLEN(), TEST_MULTIPLE_FRAMES_RESULT_FRAME_2_DATA);
        defragmentationProvider.defragment(Flowable.fromIterable(TEST_MULTIPLE_FRAMES_FRAGMENTS), TEST_MULTIPLE_FRAMES_MAXLEN).test().assertValues(TEST_MULTIPLE_FRAMES_RESULT_FRAME_0, TEST_MULTIPLE_FRAMES_RESULT_FRAME_1, TEST_MULTIPLE_FRAMES_RESULT_FRAME_2);
    }

    @Test
    public void transformsFragmentsWithTooSmallMaxLenWithErrors() throws BleException {
        int TEST_TOO_SMALL_MAXLEN_MAXLEN = 2;
        Fragment TEST_TOO_SMALL_MAXLEN_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x6a, new byte[]{});
        Fragment TEST_TOO_SMALL_MAXLEN_CONT_FRAG = new BaseContinuationFragment((byte) 0x02, new byte[]{(byte) 0xa3, (byte) 0x1});

        defragmentationProvider.defragment(Flowable.just(TEST_TOO_SMALL_MAXLEN_INIT_FRAG, TEST_TOO_SMALL_MAXLEN_CONT_FRAG), TEST_TOO_SMALL_MAXLEN_MAXLEN).test().assertError(OtherException.class);
    }

    @Test
    public void transformsIncompleteFrameWithNoValues() throws BleException {
        int TEST_INCOMPLETE_FRAME_MAXLEN = 32;
        Fragment TEST_INCOMPLETE_FRAME_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x6a, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8=", Base64.DEFAULT));
        Fragment TEST_INCOMPLETE_FRAME_CONT_FRAG = new BaseContinuationFragment((byte) 0, Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84A==", Base64.DEFAULT));

        defragmentationProvider.defragment(Flowable.just(TEST_INCOMPLETE_FRAME_INIT_FRAG, TEST_INCOMPLETE_FRAME_CONT_FRAG), TEST_INCOMPLETE_FRAME_MAXLEN).test().assertNoValues();
    }

    @Test
    public void transformsMoreDataInThanSpecifiedInHeaderWithErrors() throws BleException {
        int TEST_MORE_DATA_THAN_SPECIFIED_MAXLEN = 32;
        Fragment TEST_MORE_DATA_THAN_SPECIFIED_INIT_FRAG = new BaseInitializationFragment((byte) 0x83, (byte) 0x00, (byte) 0x22, Base64.decode("AqMBa3dlYmF1dGhuLmlvAlggKHoBf5KQFW9lYY8=", Base64.DEFAULT));
        Fragment TEST_MORE_DATA_THAN_SPECIFIED_CONT_FRAG = new BaseContinuationFragment((byte) 0, Base64.decode("E0LrllMuV2p4hgolzDsJy7CVk/e5A4GiYmlkWCB84A==", Base64.DEFAULT));

        defragmentationProvider.defragment(Flowable.just(TEST_MORE_DATA_THAN_SPECIFIED_INIT_FRAG, TEST_MORE_DATA_THAN_SPECIFIED_CONT_FRAG), TEST_MORE_DATA_THAN_SPECIFIED_MAXLEN).test().assertError(InvalidLengthException.class);
    }

    @Test
    public void transformsMultipleInitializationFragmentsWithErrors() throws BleException {
        int TEST_MORE_DATA_THAN_SPECIFIED_MAXLEN = 32;
        Fragment TEST_MULTIPLE_INIT_FRAGS_INIT_FRAG_0 = new BaseInitializationFragment((byte) 0x81, (byte) 0x00, (byte) 0x8c, Base64.decode(DATA, Base64.DEFAULT));
        Fragment TEST_MULTIPLE_INIT_FRAGS_INIT_FRAG_1 = new BaseInitializationFragment((byte) 0x81, (byte) 0x00, (byte) 0x6a, Base64.decode(DATA, Base64.DEFAULT));

        defragmentationProvider.defragment(Flowable.just(TEST_MULTIPLE_INIT_FRAGS_INIT_FRAG_0, TEST_MULTIPLE_INIT_FRAGS_INIT_FRAG_1), TEST_MORE_DATA_THAN_SPECIFIED_MAXLEN).test().assertError(OtherException.class);
    }
}
