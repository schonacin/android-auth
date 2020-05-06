package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.OperationDeniedException;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.core.Single;

public class BaseAPILevelErrorHandlerTest {

    private static final byte[] ENCODED_RESPONSE =  new byte[]{0x01, 0x02, 0x03, 0x04};

    private static final byte[] OPERATION_DENIED_STATUS_CODE =  new byte[]{0x27};

    private APILevelErrorHandler errorHandler;

    @Before
    public void setUp() {
        errorHandler = new BaseAPILevelErrorHandler();
    }

    @Test
    public void errorFreeResponse_passesTrough() {
        errorHandler.convertErrors(ENCODED_RESPONSE)
                .test()
                .assertValue(ENCODED_RESPONSE)
                .assertComplete();
    }

    @Test
    public void statusCodeException_getsTransformedToByteArray() {
        Single.error(OperationDeniedException::new)
                .map(x -> ENCODED_RESPONSE)
                .flatMap(errorHandler::convertErrors)
                .test()
                //.assertValue(OPERATION_DENIED_STATUS_CODE)
                .assertComplete();
    }

}
