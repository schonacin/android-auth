package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import android.util.Base64;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialDescriptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class CborSerializerTest {

    private static PublicKeyCredentialDescriptor descriptor1 = new BasePublicKeyCredentialDescriptor(new byte[]{0,0,0,1});
    private static PublicKeyCredentialDescriptor descriptor2 = new BasePublicKeyCredentialDescriptor(new byte[]{0,0,0,2});
    private static PublicKeyCredentialDescriptor descriptor3 = new BasePublicKeyCredentialDescriptor(new byte[]{0,0,0,3});

    @Test
    public void list_serializesCorrectly() {
        List<PublicKeyCredentialDescriptor> allowList = new LinkedList<>();
        allowList.add(descriptor1);
        allowList.add(descriptor3);
        allowList.add(descriptor2);

        final byte[] ENCODED_GET_ASSERTION_RESPONSE = Base64.decode("g6JiaWREAAAAAWR0eXBlanB1YmxpYy1rZXmiYmlkRAAAAANkdHlwZWpwdWJsaWMta2V5omJpZEQAAAACZHR5cGVqcHVibGljLWtleQ==", Base64.DEFAULT);

        CborSerializer.serializeOther(allowList)
                .map(input -> Base64.encodeToString(input, Base64.DEFAULT))
                .test()
                .assertComplete()
                .assertValueCount(1)
                .assertValue(Base64.encodeToString(ENCODED_GET_ASSERTION_RESPONSE, Base64.DEFAULT));
    }

}
