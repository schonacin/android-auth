package com.blue_unicorn.android_auth_lib;

import com.blue_unicorn.android_auth_lib.cbor.BaseCborDecode;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;

import org.junit.Test;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;

public class BaseCborDecodeTest {

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }


    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    @Test
    public void makeCredentialRequest_transformsCorrectly() {

        String reqString = "01a5015820796257f1eff073c1a30ab990ea3c8a8bef607d46d20fa5d053db743081215c2e02a26269646b776562617574686e2e696f646e616d656b776562617574686e2e696f03a36269644a969b0100000000000000646e616d65781c617364617364617364617364727767776572676173646173646173646b646973706c61794e616d65781c61736461736461736461736472776777657267617364617364617364048aa263616c672664747970656a7075626c69632d6b6579a263616c67382264747970656a7075626c69632d6b6579a263616c67382364747970656a7075626c69632d6b6579a263616c6739010064747970656a7075626c69632d6b6579a263616c6739010164747970656a7075626c69632d6b6579a263616c6739010264747970656a7075626c69632d6b6579a263616c67382464747970656a7075626c69632d6b6579a263616c67382564747970656a7075626c69632d6b6579a263616c67382664747970656a7075626c69632d6b6579a263616c672764747970656a7075626c69632d6b65790580";
        byte[] req = decodeHexString(reqString);

        TestObserver<RequestObject> subscriber = new TestObserver<>();
        Observable<RequestObject> test = new BaseCborDecode().decode(req);

        test.subscribe(subscriber);

        subscriber.assertNoErrors();

    }

}
