package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.fido.FidoObject;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import io.reactivex.rxjava3.core.Single;

public final class BaseCborDecode implements CborDecode {

    private CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    public Single<FidoObject> decode(byte[] input) {
       return Single.just(input)
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
       String jsonString = CBORObject.DecodeFromBytes(data, ENCODE_OPTIONS).ToJSONString();
       return new BaseAuthInputMapper(cmd, jsonString);
    }

}
