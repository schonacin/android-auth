package com.blue_unicorn.android_auth_lib;

import com.blue_unicorn.android_auth_lib.cbor.BaseCborDecode;
import com.blue_unicorn.android_auth_lib.exception.InvalidCmdException;
import com.blue_unicorn.android_auth_lib.exception.InvalidLenException;
import com.blue_unicorn.android_auth_lib.exception.InvalidParException;
import com.blue_unicorn.android_auth_lib.fido.BaseGetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseGetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseMakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.BasePublicKeyCredentialRpEntity;
import com.blue_unicorn.android_auth_lib.fido.BasePublicKeyCredentialUserEntity;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.TestObserver;

public class BaseCborDecodeTest {

    private byte[] rawMakeCredentialRequest = new byte[]{(byte)0x1, (byte)0xa5, (byte)0x1, (byte)0x58, (byte)0x20, (byte)0xcc, (byte)0x54, (byte)0x6f, (byte)0xd5, (byte)0x8b, (byte)0x40, (byte)0x83, (byte)0x80, (byte)0xb, (byte)0x60, (byte)0x9e, (byte)0xe1, (byte)0xa, (byte)0x9e, (byte)0x7d, (byte)0x68, (byte)0x33, (byte)0xdc, (byte)0x9e, (byte)0x54, (byte)0x53, (byte)0x40, (byte)0x91, (byte)0xbc, (byte)0x7e, (byte)0xa5, (byte)0xf6, (byte)0x9d, (byte)0x36, (byte)0x8a, (byte)0x4d, (byte)0x7c, (byte)0x2, (byte)0xa2, (byte)0x62, (byte)0x69, (byte)0x64, (byte)0x6b, (byte)0x77, (byte)0x65, (byte)0x62, (byte)0x61, (byte)0x75, (byte)0x74, (byte)0x68, (byte)0x6e, (byte)0x2e, (byte)0x69, (byte)0x6f, (byte)0x64, (byte)0x6e, (byte)0x61, (byte)0x6d, (byte)0x65, (byte)0x6b, (byte)0x77, (byte)0x65, (byte)0x62, (byte)0x61, (byte)0x75, (byte)0x74, (byte)0x68, (byte)0x6e, (byte)0x2e, (byte)0x69, (byte)0x6f, (byte)0x3, (byte)0xa3, (byte)0x62, (byte)0x69, (byte)0x64, (byte)0x4a, (byte)0xb7, (byte)0x99, (byte)0x1, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x64, (byte)0x6e, (byte)0x61, (byte)0x6d, (byte)0x65, (byte)0x64, (byte)0x68, (byte)0x61, (byte)0x68, (byte)0x61, (byte)0x6b, (byte)0x64, (byte)0x69, (byte)0x73, (byte)0x70, (byte)0x6c, (byte)0x61, (byte)0x79, (byte)0x4e, (byte)0x61, (byte)0x6d, (byte)0x65, (byte)0x64, (byte)0x68, (byte)0x61, (byte)0x68, (byte)0x61, (byte)0x4, (byte)0x8a, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x26, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x38, (byte)0x22, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x38, (byte)0x23, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x39, (byte)0x1, (byte)0x0, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x39, (byte)0x1, (byte)0x1, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x39, (byte)0x1, (byte)0x2, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x38, (byte)0x24, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x38, (byte)0x25, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x38, (byte)0x26, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x63, (byte)0x61, (byte)0x6c, (byte)0x67, (byte)0x27, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0x5, (byte)0x80};
    private byte[] rawGetAssertionRequest = new byte[]{(byte)0x2, (byte)0xa4, (byte)0x1, (byte)0x6b, (byte)0x65, (byte)0x78, (byte)0x61, (byte)0x6d, (byte)0x70, (byte)0x6c, (byte)0x65, (byte)0x2e, (byte)0x63, (byte)0x6f, (byte)0x6d, (byte)0x2, (byte)0x58, (byte)0x20, (byte)0x68, (byte)0x71, (byte)0x34, (byte)0x96, (byte)0x82, (byte)0x22, (byte)0xec, (byte)0x17, (byte)0x20, (byte)0x2e, (byte)0x42, (byte)0x50, (byte)0x5f, (byte)0x8e, (byte)0xd2, (byte)0xb1, (byte)0x6a, (byte)0xe2, (byte)0x2f, (byte)0x16, (byte)0xbb, (byte)0x5, (byte)0xb8, (byte)0x8c, (byte)0x25, (byte)0xdb, (byte)0x9e, (byte)0x60, (byte)0x26, (byte)0x45, (byte)0xf1, (byte)0x41, (byte)0x3, (byte)0x82, (byte)0xa2, (byte)0x62, (byte)0x69, (byte)0x64, (byte)0x58, (byte)0x40, (byte)0xf2, (byte)0x20, (byte)0x6, (byte)0xde, (byte)0x4f, (byte)0x90, (byte)0x5a, (byte)0xf6, (byte)0x8a, (byte)0x43, (byte)0x94, (byte)0x2f, (byte)0x2, (byte)0x4f, (byte)0x2a, (byte)0x5e, (byte)0xce, (byte)0x60, (byte)0x3d, (byte)0x9c, (byte)0x6d, (byte)0x4b, (byte)0x3d, (byte)0xf8, (byte)0xbe, (byte)0x8, (byte)0xed, (byte)0x1, (byte)0xfc, (byte)0x44, (byte)0x26, (byte)0x46, (byte)0xd0, (byte)0x34, (byte)0x85, (byte)0x8a, (byte)0xc7, (byte)0x5b, (byte)0xed, (byte)0x3f, (byte)0xd5, (byte)0x80, (byte)0xbf, (byte)0x98, (byte)0x8, (byte)0xd9, (byte)0x4f, (byte)0xcb, (byte)0xee, (byte)0x82, (byte)0xb9, (byte)0xb2, (byte)0xef, (byte)0x66, (byte)0x77, (byte)0xaf, (byte)0xa, (byte)0xdc, (byte)0xc3, (byte)0x58, (byte)0x52, (byte)0xea, (byte)0x6b, (byte)0x9e, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0xa2, (byte)0x62, (byte)0x69, (byte)0x64, (byte)0x58, (byte)0x32, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x3, (byte)0x64, (byte)0x74, (byte)0x79, (byte)0x70, (byte)0x65, (byte)0x6a, (byte)0x70, (byte)0x75, (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63, (byte)0x2d, (byte)0x6b, (byte)0x65, (byte)0x79, (byte)0x5, (byte)0xa1, (byte)0x62, (byte)0x75, (byte)0x76, (byte)0xf5};
    private byte[] rawGetInfoRequest = new byte[]{(byte)0x4};
    private byte[] rawInvalidCmdRequest = new byte[]{(byte)0xff};
    private byte[] rawEmptyRequest = new byte[]{};
    private byte[] rawInvalidParRequest = new byte[]{(byte)0x1, (byte)0x77};

    @Test
    public void makeCredentialRequest_transformsWithNoErrors() {

        TestObserver<RequestObject> subscriber = new TestObserver<>();
        Observable<RequestObject> test = new BaseCborDecode().decode(rawMakeCredentialRequest);

        test.subscribe(subscriber);

        subscriber.assertNoErrors();

    }

    @Test
    public void makeCredentialRequest_transformsWithRightValues() {
        Observable<RequestObject> test = new BaseCborDecode().decode(rawMakeCredentialRequest);
        List<RequestObject> results = new ArrayList<>();

        Disposable testDisposable =  test.subscribe(results::add);

        assertThat(results.size(), is(1));
        assertThat(results.get(0), instanceOf(BaseMakeCredentialRequest.class));

        BaseMakeCredentialRequest transformedBaseMakeCredentialRequest = (BaseMakeCredentialRequest)results.get(0);

        byte[] clientDataHash = new byte[]{(byte)0xCC, (byte)0x54, (byte)0x6F, (byte)0xD5, (byte)0x8B, (byte)0x40, (byte)0x83, (byte)0x80, (byte)0x0B, (byte)0x60, (byte)0x9E, (byte)0xE1, (byte)0x0A, (byte)0x9E, (byte)0x7D, (byte)0x68, (byte)0x33, (byte)0xDC, (byte)0x9E, (byte)0x54, (byte)0x53, (byte)0x40, (byte)0x91, (byte)0xBC, (byte)0x7E, (byte)0xA5, (byte)0xF6, (byte)0x9D, (byte)0x36, (byte)0x8A, (byte)0x4D, (byte)0x7C};
        assertThat(transformedBaseMakeCredentialRequest.getClientDataHash(), is(clientDataHash));

        BasePublicKeyCredentialUserEntity user = transformedBaseMakeCredentialRequest.getUser();
        assertThat(user.getName(), is("haha"));
        assertThat(user.getDisplayName(), is("haha"));
        assertThat(user.getId(), is(new byte[]{-73, -103, 1, 0, 0, 0, 0, 0, 0 ,0}));

        BasePublicKeyCredentialRpEntity rp = transformedBaseMakeCredentialRequest.getRp();
        assertThat(rp.getId(), is("webauthn.io"));
        assertThat(rp.getName(), is("webauthn.io"));

        assertThat(transformedBaseMakeCredentialRequest.getPubKeyCredParams().length, is(10));
        assertThat(transformedBaseMakeCredentialRequest.getExcludeList().size(), is(0));

        testDisposable.dispose();
    }

    @Test
    public void getAssertionRequest_transformsWithNoErrors() {

        TestObserver<RequestObject> subscriber = new TestObserver<>();
        Observable<RequestObject> test = new BaseCborDecode().decode(rawGetAssertionRequest);

        test.subscribe(subscriber);

        subscriber.assertNoErrors();

    }

    @Test
    public void getAssertionRequest_transformsWithRightValues() {
        Observable<RequestObject> test = new BaseCborDecode().decode(rawGetAssertionRequest);
        List<RequestObject> results = new ArrayList<>();

        Disposable testDisposable =  test.subscribe(results::add);

        assertThat(results.size(), is(1));
        assertThat(results.get(0), instanceOf(BaseGetAssertionRequest.class));

        BaseGetAssertionRequest transformedBaseGetAssertionRequest = (BaseGetAssertionRequest) results.get(0);

        byte[] clientDataHash = new byte[]{(byte)0x68, (byte)0x71, (byte)0x34, (byte)0x96, (byte)0x82, (byte)0x22, (byte)0xEC, (byte)0x17, (byte)0x20, (byte)0x2E, (byte)0x42, (byte)0x50, (byte)0x5F, (byte)0x8E, (byte)0xD2, (byte)0xB1, (byte)0x6A, (byte)0xE2, (byte)0x2F, (byte)0x16, (byte)0xBB, (byte)0x05, (byte)0xB8, (byte)0x8C, (byte)0x25, (byte)0xDB, (byte)0x9E, (byte)0x60, (byte)0x26, (byte)0x45, (byte)0xF1, (byte)0x41};
        assertThat(transformedBaseGetAssertionRequest.getClientDataHash(), is(clientDataHash));

        assertThat(transformedBaseGetAssertionRequest.getRpId(), is("example.com"));

        assertThat(transformedBaseGetAssertionRequest.getAllowList().size(), is(2));

        assertThat(transformedBaseGetAssertionRequest.getOptions().size(), is(1));

        testDisposable.dispose();
    }

    @Test
    public void getInfoRequest_transformsWithNoErrors() {

        TestObserver<RequestObject> subscriber = new TestObserver<>();
        Observable<RequestObject> test = new BaseCborDecode().decode(rawGetInfoRequest);

        test.subscribe(subscriber);

        subscriber.assertNoErrors();

    }

    @Test
    public void getInfoRequest_transformsWithRightValues() {
        Observable<RequestObject> test = new BaseCborDecode().decode(rawGetInfoRequest);
        List<RequestObject> results = new ArrayList<>();

        Disposable testDisposable =  test.subscribe(results::add);

        assertThat(results.size(), is(1));
        assertThat(results.get(0), instanceOf(BaseGetInfoRequest.class));

        testDisposable.dispose();
    }

    @Test
    public void invalidCmdRequest_fails() {
        TestObserver<RequestObject> subscriber = new TestObserver<>();
        Observable<RequestObject> test = new BaseCborDecode().decode(rawInvalidCmdRequest);

        test.subscribe(subscriber);

        subscriber.assertError(InvalidCmdException.class);
    }

    @Test
    public void emptyRequest_fails() {
        TestObserver<RequestObject> subscriber = new TestObserver<>();
        Observable<RequestObject> test = new BaseCborDecode().decode(rawEmptyRequest);

        test.subscribe(subscriber);

        subscriber.assertError(InvalidLenException.class);
    }

    @Test
    public void invalidParRequest_fails() {
        TestObserver<RequestObject> subscriber = new TestObserver<>();
        Observable<RequestObject> test = new BaseCborDecode().decode(rawInvalidParRequest);

        test.subscribe(subscriber);

        subscriber.assertError(InvalidParException.class);
    }

}
