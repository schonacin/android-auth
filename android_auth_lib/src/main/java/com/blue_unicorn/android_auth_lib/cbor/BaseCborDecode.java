package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.fido.RequestObject;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.JSONOptions;

import io.reactivex.rxjava3.core.Observable;

public final class BaseCborDecode implements CborDecode {

    private CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;
    private JSONOptions JSON_OPTIONS = new JSONOptions("base64padding=true;replacesurrogates=false");

    public Observable<RequestObject> decode(byte[] input) {
       return Observable.just(input)
               .map(this::splitInput)
               .map(this::createMapper)
               .map(BaseAuthInputMapper::mapRespectiveCommand);
    }

    private BaseAuthRequest splitInput(byte[] input) {
       /* TODO error handling */
       byte cmd = input[0];

       byte[] data = new byte[input.length -1];
       System.arraycopy(input, 1, data, 0, data.length);

       return new BaseAuthRequest(cmd, data);
    }

    private BaseAuthInputMapper createMapper(BaseAuthRequest req) {
       byte cmd = req.getCmd();
       byte[] data = req.getData();
       CBORObject cbor = CBORObject.DecodeFromBytes(data, ENCODE_OPTIONS);
       String jsonString = cbor.ToJSONString(JSON_OPTIONS);
       return new BaseAuthInputMapper(cmd, jsonString);
    }

}
