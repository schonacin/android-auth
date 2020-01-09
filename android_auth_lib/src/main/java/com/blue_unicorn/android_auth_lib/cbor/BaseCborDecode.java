package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.exception.InvalidLenException;
import com.blue_unicorn.android_auth_lib.exception.InvalidParException;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import io.reactivex.rxjava3.core.Observable;

public final class BaseCborDecode implements CborDecode {

    private CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    public Observable<RequestObject> decode(byte[] input) {
       return splitInput(input)
               .flatMap(this::createMapper)
               .flatMap(AuthInputMapper::mapRespectiveCommand);

    }

    private Observable<AuthRequest> splitInput(byte[] input) {

       if (input.length == 0)
           return Observable.error(new InvalidLenException());
       else {
           byte cmd = input[0];

           byte[] data = new byte[input.length -1];
           System.arraycopy(input, 1, data, 0, data.length);

           return Observable.just(new BaseAuthRequest(cmd, data));
       }

    }

    private Observable<AuthInputMapper> createMapper(AuthRequest req) {

       byte cmd = req.getCmd();
       byte[] data = req.getData();

       // specific commands can have zero parameters (getInfo)
       if (data.length == 0)
           return Observable.just(new BaseAuthInputMapper(cmd, null));


       try {
           CBORObject cbor = CBORObject.DecodeFromBytes(data, ENCODE_OPTIONS);
           String jsonString = cbor.ToJSONString();

           return Observable.just(new BaseAuthInputMapper(cmd, jsonString));
       } catch(Exception e) {
           // if any error encounters during the encoding process we assume that the request is invalid
           return Observable.error(new InvalidParException());
       }


    }

}
